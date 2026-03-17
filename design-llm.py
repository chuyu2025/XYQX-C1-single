
# api_call_example.py (多轮对话版)
from openai import OpenAI
import re
import json
from datetime import datetime
import os
import math


def save_lattice_to_json(lattice_value: float, coords: list,filename, dest_dir: str = 'design-AVHI' ) -> str:
    """Save the extracted lattice (float) and coordinates (list of float pairs) into JSON.

    Returns the path to the saved file.
    """
    data = [
        {
            "Lattice constant": lattice_value,
            "Coordinate": coords
        }
    ]

    #filename = f"sim-AVHI.json"
    path = os.path.join(dest_dir, filename)
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    return path


def extract_lattice_and_coords(text: str):
    """Extract lattice constant as float and coordinates as list of float pairs.

    Returns (lattice_value: float, coords_list: list) or (None, None) if not found/parsable.
    """
    # Look for 'Lattice constant: <value>' (captures numbers, may include units)
    lat_match = re.search(r"Lattice\s*constant\s*:\s*([^,;\n]+)", text, flags=re.IGNORECASE)
    coord_match = re.search(r"coordinate\s*[:：]\s*(.+)$", text, flags=re.IGNORECASE)

    if not lat_match or not coord_match:
        return None, None

    lat_raw = lat_match.group(1).strip()
    # extract numeric part from lattice (e.g., '12.34 mm' -> '12.34')
    num_match = re.search(r"[-+]?[0-9]*\.?[0-9]+", lat_raw)
    if not num_match:
        return None, None
    try:
        lattice_value = float(num_match.group(0))
    except Exception:
        return None, None

    coords_raw = coord_match.group(1).strip()
    # Trim trailing punctuation
    coords_raw = coords_raw.rstrip('.;\n ')

    # Find all bracketed pairs like [x,y]
    pairs = re.findall(r"\[\s*([^\]]+)\s*\]", coords_raw)
    coords_list = []
    if pairs:
        for p in pairs:
            parts = re.split(r"\s*,\s*", p.strip())
            if len(parts) >= 2:
                try:
                    x = float(parts[0])
                    y = float(parts[1])
                    coords_list.append([x, y])
                except Exception:
                    # skip unparsable pair
                    continue
    else:
        # fallback: split by semicolon then comma
        items = re.split(r"\s*;\s*", coords_raw)
        for item in items:
            if not item:
                continue
            parts = re.split(r"\s*,\s*", item.strip())
            if len(parts) >= 2:
                try:
                    x = float(parts[0].strip(' []'))
                    y = float(parts[1].strip(' []'))
                    coords_list.append([x, y])
                except Exception:
                    continue

    if not coords_list:
        return None, None

    return lattice_value, coords_list


# helper: reflect points across line through origin with slope m
def reflect_across_line(points, m: float):
    u_len = math.hypot(1.0, m)
    ux = 1.0 / u_len
    uy = m / u_len
    reflected = []
    for x, y in points:
        dot = x * ux + y * uy
        rx = 2 * dot * ux - x
        ry = 2 * dot * uy - y
        reflected.append([rx, ry])
    return reflected


# helper: rotate points clockwise by degrees
def rotate_clockwise(points, degrees: float):
    theta = math.radians(-degrees)  # negative for clockwise
    cos_t = math.cos(theta)
    sin_t = math.sin(theta)
    rotated = []
    for x, y in points:
        rx = cos_t * x - sin_t * y
        ry = sin_t * x + cos_t * y
        rotated.append([rx, ry])
    return rotated


# helper: deduplicate points with rounding tolerance
def dedupe_points(points, ndigits=8):
    seen = set()
    unique = []
    for x, y in points:
        key = (round(x, ndigits), round(y, ndigits))
        if key not in seen:
            seen.add(key)
            unique.append([float(key[0]), float(key[1])])
    return unique


def round_points(points, ndigits=2):
    """Round each coordinate in points to `ndigits` decimal places."""
    rounded = []
    for x, y in points:
        try:
            rx = round(float(x), ndigits)
            ry = round(float(y), ndigits)
            rounded.append([rx, ry])
        except Exception:
            continue
    return rounded


def generate_design_from_prompt(prompt: str, api_key: str = "0", base_url: str = "http://127.0.0.1:8000/v1", model_path: str = None, save_json: bool = False, dest_dir: str = 'design-AVHI'):
    """Send a single prompt to the local LLM server, extract lattice and coordinates,
    perform the symmetric transformations and return (lat_val, coords_6).

    Returns (lat_val, coords_6) or (None, None) on failure.
    """
    if model_path is None:
        model_path = r"F:\\WangYinchu\\LLM\\model\\chuchulove\\XYQX-C1-2025"

    client = OpenAI(api_key=api_key, base_url=base_url)

    messages = [{"role": "user", "content": prompt}]
    try:
        result = client.chat.completions.create(
            messages=messages,
            model=model_path
        )
    except Exception as e:
        raise RuntimeError(f"LLM request failed: {e}")

    # Extract assistant content (same robust logic as in main)
    content = ""
    choice = None
    try:
        choice = result.choices[0]
    except Exception:
        choice = None

    if choice is not None:
        if hasattr(choice, "message"):
            msg = choice.message
            if isinstance(msg, dict):
                content = msg.get("content", "")
            else:
                content = getattr(msg, "content", "") or ""
        else:
            if isinstance(choice, dict):
                content = choice.get("text", "") or choice.get("message", {}).get("content", "")
            else:
                content = getattr(choice, "text", None) or str(choice)

    if content is None:
        content = ""

    # Parse lattice and coordinates from assistant content
    lat_val, coords = extract_lattice_and_coords(content)
    if not lat_val or not coords:
        return None, None

    # Save initial detection if requested
    if save_json:
        os.makedirs(dest_dir, exist_ok=True)
        save_lattice_to_json(lat_val, coords, 'sim-AVHI.json', dest_dir=dest_dir)

    # Follow the same transform logic as in interactive main()
    try:
        coords_1 = list(reversed(coords))
        m = math.sqrt(3)
        coords_2 = reflect_across_line(coords_1, m)
        coords_3 = coords + coords_2[1:]
        coords_4 = rotate_clockwise(coords_3, 120)
        coords_5 = rotate_clockwise(coords_3, 240)
        combined = coords_3 + coords_4[1:] + coords_5[1:-1]
        coords_6 = dedupe_points(combined)
        coords_6 = round_points(coords_6, 2)

        if save_json:
            save_lattice_to_json(lat_val, coords_6, 'com-AVHI.json', dest_dir=dest_dir)

        return lat_val, coords_6

    except Exception as e:
        raise RuntimeError(f"Failed to post-process LLM coordinates: {e}")


def main():
    print("Multi-turn chat with model. Type 'exit' or 'quit' to stop.")

    # 请根据实际情况调整 api_key 和 base_url
    client = OpenAI(api_key="0", base_url="http://127.0.0.1:8000/v1")
    model_path = r"F:\\WangYinchu\\LLM\\model\\chuchulove\\XYQX-C1-2025"

    # Conversation history (start empty or with system prompt if desired)
    messages = []

    try:
        while True:

            user_input = input("You: ").strip()
            if not user_input:
                continue
            if user_input.lower() in ("exit", "quit"):
                print("Exiting.")
                break

            # Add user message to history
            messages.append({"role": "user", "content": user_input})

            # Send full conversation to the model so it has context
            result = client.chat.completions.create(
                messages=messages,
                model=model_path
            )

            # Extract assistant content robustly (handle dicts or objects)
            content = ""
            choice = None
            try:
                choice = result.choices[0]
            except Exception:
                choice = None

            if choice is not None:
                # case: choice.message exists (dict or object)
                if hasattr(choice, "message"):
                    msg = choice.message
                    if isinstance(msg, dict):
                        content = msg.get("content", "")
                    else:
                        content = getattr(msg, "content", "") or ""
                else:
                    # fallback: try common fields
                    if isinstance(choice, dict):
                        content = choice.get("text", "") or choice.get("message", {}).get("content", "")
                    else:
                        content = getattr(choice, "text", None) or str(choice)

            if content is None:
                content = ""

            # Print assistant reply and add to history
            print("Assistant:", content)
            messages.append({"role": "assistant", "content": content})

            # After printing, check if assistant content matches the lattice/coordinate pattern
            lat_val, coords = extract_lattice_and_coords(content)
            filename = "sim-AVHI.json"
            saved_path = save_lattice_to_json(lat_val, coords,filename )
            print(f"Detected lattice info — saved JSON (coords) to: {saved_path}")
            if lat_val and coords:
                try:
                    # coords is a list of [x,y] floats
                    coords_1 = list(reversed(coords))

                    # reflect across line y = sqrt(3) * x
                    m = math.sqrt(3)
                    coords_2 = reflect_across_line(coords_1, m)

                    # coords_3 = coords_1 + coords_2
                    coords_3 = coords + coords_2[1:]

                    # coords_4 = coords_3 rotated clockwise 120°
                    coords_4 = rotate_clockwise(coords_3, 120)

                    # coords_5 = coords_3 rotated clockwise 240°
                    coords_5 = rotate_clockwise(coords_3, 240)

                    # coords_6 = combine coords_3, coords_4, coords_5 and dedupe
                    combined = coords_3 + coords_4[1:] + coords_5[1:-1]
                    coords_6 = dedupe_points(combined)

                    # Round coords_6 to 2 decimal places
                    coords_6 = round_points(coords_6, 2)

                    # Save final coords_6 along with lattice value
                    filename = "com-AVHI.json"
                    saved_path = save_lattice_to_json(lat_val, coords_6, filename)
                    print(f"Detected lattice info — saved JSON (coords_6) to: {saved_path}")

                    # also print summaries
                    print(f"counts: original={len(coords)}, c1={len(coords_1)}, c2={len(coords_2)}, c3={len(coords_3)}, c4={len(coords_4)}, c5={len(coords_5)}, c6={len(coords_6)}")
                except Exception as e:
                    print(f"Failed processing lattice coordinates: {e}")

    except KeyboardInterrupt:
        print("\nInterrupted. Exiting.")


if __name__ == "__main__":
    main()