# XYQX-C1

AI-driven acoustic metamaterial inverse design platform for AVHI (Acoustic Valley Hall Insulator), with chat interaction, automated geometry generation, and COMSOL simulation export.

![Python](https://img.shields.io/badge/Python-3.9%2B-3776AB?logo=python&logoColor=white)
![Flask](https://img.shields.io/badge/Flask-Web_API-000000?logo=flask&logoColor=white)
![COMSOL](https://img.shields.io/badge/COMSOL-Integrated-00599C)
![Status](https://img.shields.io/badge/Status-Research_Prototype-orange)
![License](https://img.shields.io/badge/License-TBD-lightgrey)

## Key Features

- Conversational AVHI design (LLM intent parsing + design trigger)
- Multi-user web sessions with isolated history
- COMSOL-based simulation workflow with exported artifacts
- Ready-to-download outputs: PNG, TXT, STL, MPH

## Architecture

```mermaid
flowchart LR
    A[Web UI] --> B[Flask API]
    B --> C[XYQX_C1_single.py Agent]
    C --> D[LLM Service OpenAI-compatible]
    C --> E[chu.py Design Pipeline]
    E --> F[Data Files band_data TI_data]
    E --> G[COMSOL mph]
    G --> H[Artifacts PNG TXT STL MPH]
    H --> A
```

## Quick Start

```bash
pip install -r requirements.txt
python XYQX_C1_single.py
```

Open http://localhost:5000

## Main Endpoints

- POST /api/design
- POST /api/chat
- POST /api/reset
- GET /api/stats
- GET /api/download/<filename>

> Due to GitHub's storage limitations, the fine-tuned AVHI structural design large language model cannot be uploaded. To use the full version of the design function, please visit the following URL:
> https://xyqx-c1.cpolar.io/

## Repository Notes

- Default local inference endpoint: http://127.0.0.1:8000/v1
- API key is loaded from .env.sample (OPENAI_API_KEY)
- This repository currently has no declared open-source license
