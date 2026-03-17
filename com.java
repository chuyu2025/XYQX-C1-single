/*
 * com.java
 */

import com.comsol.model.*;
import com.comsol.model.util.*;

/** Model exported on Nov 4 2025, 15:32 by COMSOL 6.1.0.252. */
public class com {

  public static Model run() {
    Model model = ModelUtil.create("Model");

    model.modelPath("D:\\ACODE_design\\XTQX-C1-project\\XYQX_C1_english");

    model.label("ATI.mph");

    model.param().set("a", "21[mm]", "\u6676\u683c\u5e38\u6570");
    model.param().set("rate1", "0.9");
    model.param().set("rate2", "0.5");
    model.param().set("k", "2");
    model.param().set("rate3", "0.7");
    model.param().set("rate4", "0.3");

    model.component().create("comp1", true);

    model.component("comp1").geom().create("geom1", 2);

    model.result().table().create("evl2", "Table");
    model.result().evaluationGroup().create("std1EvgFrq", "EvaluationGroup");
    model.result().evaluationGroup("std1EvgFrq").create("gev1", "EvalGlobal");

    model.component("comp1").func().create("pw1", "Piecewise");
    model.component("comp1").func().create("pw2", "Piecewise");
    model.component("comp1").func("pw1").set("funcname", "kx");
    model.component("comp1").func("pw1").set("arg", "k");
    model.component("comp1").func("pw1")
         .set("pieces", new String[][]{{"0", "1", "(1-k)*2*sqrt(3)*pi/(3*a)"}, {"1", "2", "(k-1)*2*sqrt(3)*pi/(3*a)"}, {"2", "3", "2*sqrt(3)*pi/(3*a)"}});
    model.component("comp1").func("pw2").set("funcname", "ky");
    model.component("comp1").func("pw2").set("arg", "k");
    model.component("comp1").func("pw2")
         .set("pieces", new String[][]{{"0", "1", "0"}, {"1", "2", "(k-1)*2*pi/(3*a)"}, {"2", "3", "(3-k)*2*pi/(3*a)"}});

    model.component("comp1").mesh().create("mesh1");

    model.component("comp1").geom("geom1").lengthUnit("mm");
    model.component("comp1").geom("geom1").create("pol1", "Polygon");
    model.component("comp1").geom("geom1").feature("pol1").label("\u6676\u80de");
    model.component("comp1").geom("geom1").feature("pol1").set("source", "table");
    model.component("comp1").geom("geom1").feature("pol1")
         .set("table", new String[][]{{"0", "0"}, {"a/(2*sqrt(3))", "a/2"}, {"-a/(2*sqrt(3))", "a/2"}});
    model.component("comp1").geom("geom1").create("pol2", "Polygon");
    model.component("comp1").geom("geom1").feature("pol2").set("source", "table");
    model.component("comp1").geom("geom1").feature("pol2")
         .set("table", new String[][]{{"0", "0"}, {"a/(2*sqrt(3))*rate1", "a/2*rate1"}, {"0", "a/2*rate3"}, {"-a/(2*sqrt(3))*rate2", "a/2*rate2"}});
    model.component("comp1").geom("geom1").create("mir1", "Mirror");
    model.component("comp1").geom("geom1").feature("mir1").set("keep", true);
    model.component("comp1").geom("geom1").feature("mir1").set("pos", new int[]{0, 0});
    model.component("comp1").geom("geom1").feature("mir1").set("axis", new String[]{"sqrt(3)", "-1"});
    model.component("comp1").geom("geom1").feature("mir1").selection("input").set("pol1", "pol2");
    model.component("comp1").geom("geom1").create("rot1", "Rotate");
    model.component("comp1").geom("geom1").feature("rot1").set("keep", true);
    model.component("comp1").geom("geom1").feature("rot1").set("rot", "range(120,120,240)");
    model.component("comp1").geom("geom1").feature("rot1").selection("input").set("mir1", "pol1", "pol2");
    model.component("comp1").geom("geom1").create("dif1", "Difference");
    model.component("comp1").geom("geom1").feature("dif1").selection("input")
         .set("mir1(1)", "pol1", "rot1(1)", "rot1(2)", "rot1(5)", "rot1(6)");
    model.component("comp1").geom("geom1").feature("dif1").selection("input2")
         .set("mir1(2)", "pol2", "rot1(3)", "rot1(4)", "rot1(7)", "rot1(8)");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").geom("geom1").run("rot1");

    model.component("comp1").material().create("mat1", "Common");
    model.component("comp1").material("mat1").propertyGroup("def").func().create("eta", "Piecewise");
    model.component("comp1").material("mat1").propertyGroup("def").func().create("Cp", "Piecewise");
    model.component("comp1").material("mat1").propertyGroup("def").func().create("rho", "Analytic");
    model.component("comp1").material("mat1").propertyGroup("def").func().create("k", "Piecewise");
    model.component("comp1").material("mat1").propertyGroup("def").func().create("cs", "Analytic");
    model.component("comp1").material("mat1").propertyGroup("def").func().create("an1", "Analytic");
    model.component("comp1").material("mat1").propertyGroup("def").func().create("an2", "Analytic");
    model.component("comp1").material("mat1").propertyGroup().create("RefractiveIndex", "Refractive index");
    model.component("comp1").material("mat1").propertyGroup().create("NonlinearModel", "Nonlinear model");
    model.component("comp1").material("mat1").propertyGroup().create("idealGas", "Ideal gas");
    model.component("comp1").material("mat1").propertyGroup("idealGas").func().create("Cp", "Piecewise");

    model.component("comp1").physics().create("acpr", "PressureAcoustics", "geom1");
    model.component("comp1").physics("acpr").create("pc1", "PeriodicCondition", 1);
    model.component("comp1").physics("acpr").feature("pc1").selection().set(3, 20);
    model.component("comp1").physics("acpr").create("pc2", "PeriodicCondition", 1);
    model.component("comp1").physics("acpr").feature("pc2").selection().set(2, 24);
    model.component("comp1").physics("acpr").create("pc3", "PeriodicCondition", 1);
    model.component("comp1").physics("acpr").feature("pc3").selection().set(9, 11);

    model.result().table("evl2").label("Evaluation 2D");
    model.result().table("evl2").comments("\u4ea4\u4e92\u7684\u4e8c\u7ef4\u503c");

    model.component("comp1").view("view1").axis().set("xmin", -17.653125762939453);
    model.component("comp1").view("view1").axis().set("xmax", 17.653125762939453);
    model.component("comp1").view("view1").axis().set("ymin", -11.02500057220459);
    model.component("comp1").view("view1").axis().set("ymax", 11.02500057220459);

    model.component("comp1").material("mat1").label("Air");
    model.component("comp1").material("mat1").set("family", "air");
    model.component("comp1").material("mat1").propertyGroup("def").label("Basic");
    model.component("comp1").material("mat1").propertyGroup("def").func("eta").label("Piecewise");
    model.component("comp1").material("mat1").propertyGroup("def").func("eta").set("arg", "T");
    model.component("comp1").material("mat1").propertyGroup("def").func("eta")
         .set("pieces", new String[][]{{"200.0", "1600.0", "-8.38278E-7+8.35717342E-8*T^1-7.69429583E-11*T^2+4.6437266E-14*T^3-1.06585607E-17*T^4"}});
    model.component("comp1").material("mat1").propertyGroup("def").func("eta").set("argunit", "K");
    model.component("comp1").material("mat1").propertyGroup("def").func("eta").set("fununit", "Pa*s");
    model.component("comp1").material("mat1").propertyGroup("def").func("Cp").label("Piecewise 2");
    model.component("comp1").material("mat1").propertyGroup("def").func("Cp").set("arg", "T");
    model.component("comp1").material("mat1").propertyGroup("def").func("Cp")
         .set("pieces", new String[][]{{"200.0", "1600.0", "1047.63657-0.372589265*T^1+9.45304214E-4*T^2-6.02409443E-7*T^3+1.2858961E-10*T^4"}});
    model.component("comp1").material("mat1").propertyGroup("def").func("Cp").set("argunit", "K");
    model.component("comp1").material("mat1").propertyGroup("def").func("Cp").set("fununit", "J/(kg*K)");
    model.component("comp1").material("mat1").propertyGroup("def").func("rho").label("Analytic");
    model.component("comp1").material("mat1").propertyGroup("def").func("rho")
         .set("expr", "pA*0.02897/R_const[K*mol/J]/T");
    model.component("comp1").material("mat1").propertyGroup("def").func("rho").set("args", new String[]{"pA", "T"});
    model.component("comp1").material("mat1").propertyGroup("def").func("rho").set("fununit", "kg/m^3");
    model.component("comp1").material("mat1").propertyGroup("def").func("rho")
         .set("argunit", new String[]{"Pa", "K"});
    model.component("comp1").material("mat1").propertyGroup("def").func("rho")
         .set("plotargs", new String[][]{{"pA", "101325", "101325"}, {"T", "273.15", "293.15"}});
    model.component("comp1").material("mat1").propertyGroup("def").func("k").label("Piecewise 3");
    model.component("comp1").material("mat1").propertyGroup("def").func("k").set("arg", "T");
    model.component("comp1").material("mat1").propertyGroup("def").func("k")
         .set("pieces", new String[][]{{"200.0", "1600.0", "-0.00227583562+1.15480022E-4*T^1-7.90252856E-8*T^2+4.11702505E-11*T^3-7.43864331E-15*T^4"}});
    model.component("comp1").material("mat1").propertyGroup("def").func("k").set("argunit", "K");
    model.component("comp1").material("mat1").propertyGroup("def").func("k").set("fununit", "W/(m*K)");
    model.component("comp1").material("mat1").propertyGroup("def").func("cs").label("Analytic 2");
    model.component("comp1").material("mat1").propertyGroup("def").func("cs")
         .set("expr", "sqrt(1.4*R_const[K*mol/J]/0.02897*T)");
    model.component("comp1").material("mat1").propertyGroup("def").func("cs").set("args", new String[]{"T"});
    model.component("comp1").material("mat1").propertyGroup("def").func("cs").set("fununit", "m/s");
    model.component("comp1").material("mat1").propertyGroup("def").func("cs").set("argunit", new String[]{"K"});
    model.component("comp1").material("mat1").propertyGroup("def").func("cs")
         .set("plotargs", new String[][]{{"T", "273.15", "373.15"}});
    model.component("comp1").material("mat1").propertyGroup("def").func("an1").label("Analytic 1");
    model.component("comp1").material("mat1").propertyGroup("def").func("an1").set("funcname", "alpha_p");
    model.component("comp1").material("mat1").propertyGroup("def").func("an1")
         .set("expr", "-1/rho(pA,T)*d(rho(pA,T),T)");
    model.component("comp1").material("mat1").propertyGroup("def").func("an1").set("args", new String[]{"pA", "T"});
    model.component("comp1").material("mat1").propertyGroup("def").func("an1").set("fununit", "1/K");
    model.component("comp1").material("mat1").propertyGroup("def").func("an1")
         .set("argunit", new String[]{"Pa", "K"});
    model.component("comp1").material("mat1").propertyGroup("def").func("an1")
         .set("plotargs", new String[][]{{"pA", "101325", "101325"}, {"T", "273.15", "373.15"}});
    model.component("comp1").material("mat1").propertyGroup("def").func("an2").label("Analytic 2a");
    model.component("comp1").material("mat1").propertyGroup("def").func("an2").set("funcname", "muB");
    model.component("comp1").material("mat1").propertyGroup("def").func("an2").set("expr", "0.6*eta(T)");
    model.component("comp1").material("mat1").propertyGroup("def").func("an2").set("args", new String[]{"T"});
    model.component("comp1").material("mat1").propertyGroup("def").func("an2").set("fununit", "Pa*s");
    model.component("comp1").material("mat1").propertyGroup("def").func("an2").set("argunit", new String[]{"K"});
    model.component("comp1").material("mat1").propertyGroup("def").func("an2")
         .set("plotargs", new String[][]{{"T", "200", "1600"}});
    model.component("comp1").material("mat1").propertyGroup("def").set("thermalexpansioncoefficient", "");
    model.component("comp1").material("mat1").propertyGroup("def").set("molarmass", "");
    model.component("comp1").material("mat1").propertyGroup("def").set("bulkviscosity", "");
    model.component("comp1").material("mat1").propertyGroup("def")
         .set("thermalexpansioncoefficient", new String[]{"alpha_p(pA,T)", "0", "0", "0", "alpha_p(pA,T)", "0", "0", "0", "alpha_p(pA,T)"});
    model.component("comp1").material("mat1").propertyGroup("def").set("molarmass", "0.02897[kg/mol]");
    model.component("comp1").material("mat1").propertyGroup("def").set("bulkviscosity", "muB(T)");
    model.component("comp1").material("mat1").propertyGroup("def")
         .set("relpermeability", new String[]{"1", "0", "0", "0", "1", "0", "0", "0", "1"});
    model.component("comp1").material("mat1").propertyGroup("def")
         .set("relpermittivity", new String[]{"1", "0", "0", "0", "1", "0", "0", "0", "1"});
    model.component("comp1").material("mat1").propertyGroup("def").set("dynamicviscosity", "eta(T)");
    model.component("comp1").material("mat1").propertyGroup("def").set("ratioofspecificheat", "1.4");
    model.component("comp1").material("mat1").propertyGroup("def")
         .set("electricconductivity", new String[]{"0[S/m]", "0", "0", "0", "0[S/m]", "0", "0", "0", "0[S/m]"});
    model.component("comp1").material("mat1").propertyGroup("def").set("heatcapacity", "Cp(T)");
    model.component("comp1").material("mat1").propertyGroup("def").set("density", "rho(pA,T)");
    model.component("comp1").material("mat1").propertyGroup("def")
         .set("thermalconductivity", new String[]{"k(T)", "0", "0", "0", "k(T)", "0", "0", "0", "k(T)"});
    model.component("comp1").material("mat1").propertyGroup("def").set("soundspeed", "cs(T)");
    model.component("comp1").material("mat1").propertyGroup("def").addInput("temperature");
    model.component("comp1").material("mat1").propertyGroup("def").addInput("pressure");
    model.component("comp1").material("mat1").propertyGroup("RefractiveIndex").label("Refractive index");
    model.component("comp1").material("mat1").propertyGroup("RefractiveIndex")
         .set("n", new String[]{"1", "0", "0", "0", "1", "0", "0", "0", "1"});
    model.component("comp1").material("mat1").propertyGroup("NonlinearModel").label("Nonlinear model");
    model.component("comp1").material("mat1").propertyGroup("NonlinearModel").set("BA", "(def.gamma+1)/2");
    model.component("comp1").material("mat1").propertyGroup("idealGas").label("Ideal gas");
    model.component("comp1").material("mat1").propertyGroup("idealGas").func("Cp").label("Piecewise 2");
    model.component("comp1").material("mat1").propertyGroup("idealGas").func("Cp").set("arg", "T");
    model.component("comp1").material("mat1").propertyGroup("idealGas").func("Cp")
         .set("pieces", new String[][]{{"200.0", "1600.0", "1047.63657-0.372589265*T^1+9.45304214E-4*T^2-6.02409443E-7*T^3+1.2858961E-10*T^4"}});
    model.component("comp1").material("mat1").propertyGroup("idealGas").func("Cp").set("argunit", "K");
    model.component("comp1").material("mat1").propertyGroup("idealGas").func("Cp").set("fununit", "J/(kg*K)");
    model.component("comp1").material("mat1").propertyGroup("idealGas").set("Rs", "R_const/Mn");
    model.component("comp1").material("mat1").propertyGroup("idealGas").set("heatcapacity", "Cp(T)");
    model.component("comp1").material("mat1").propertyGroup("idealGas").set("ratioofspecificheat", "1.4");
    model.component("comp1").material("mat1").propertyGroup("idealGas").set("molarmass", "0.02897");
    model.component("comp1").material("mat1").propertyGroup("idealGas").addInput("temperature");
    model.component("comp1").material("mat1").propertyGroup("idealGas").addInput("pressure");
    model.component("comp1").material("mat1").materialType("nonSolid");

    model.component("comp1").physics("acpr").prop("MeshControl").set("SizeControlParameter", "Frequency");
    model.component("comp1").physics("acpr").prop("MeshControl")
         .set("PhysicsControlledMeshMaximumFrequency", "30000[Hz]");
    model.component("comp1").physics("acpr").feature("pc1").set("PeriodicType", "Floquet");
    model.component("comp1").physics("acpr").feature("pc1")
         .set("kFloquet", new String[][]{{"kx(k)"}, {"ky(k)"}, {"0"}});
    model.component("comp1").physics("acpr").feature("pc2").set("PeriodicType", "Floquet");
    model.component("comp1").physics("acpr").feature("pc2")
         .set("kFloquet", new String[][]{{"kx(k)"}, {"ky(k)"}, {"0"}});
    model.component("comp1").physics("acpr").feature("pc3").set("PeriodicType", "Floquet");
    model.component("comp1").physics("acpr").feature("pc3")
         .set("kFloquet", new String[][]{{"kx(k)"}, {"ky(k)"}, {"0"}});

    model.study().create("std1");
    model.study("std1").create("param", "Parametric");
    model.study("std1").create("eig", "Eigenfrequency");

    model.sol().create("sol1");
    model.sol("sol1").study("std1");
    model.sol("sol1").attach("std1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol().create("sol2");
    model.sol("sol2").study("std1");
    model.sol("sol2").label("\u53c2\u6570\u5316\u89e3 1");

    model.batch().create("p1", "Parametric");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").study("std1");

    model.result().create("pg1", "PlotGroup2D");
    model.result().create("pg2", "PlotGroup2D");
    model.result().create("pg3", "PlotGroup1D");
    model.result("pg1").set("data", "dset2");
    model.result("pg1").create("surf1", "Surface");
    model.result("pg1").create("con1", "Contour");
    model.result("pg1").feature("surf1").set("expr", "abs(acpr.p_t)");
    model.result("pg2").set("data", "dset2");
    model.result("pg2").create("surf1", "Surface");
    model.result("pg2").feature("surf1").set("expr", "acpr.Lp_t");
    model.result("pg3").create("glob1", "Global");
    model.result("pg3").feature("glob1").set("data", "dset2");

    model.study("std1").feature("param").set("pname", new String[]{"k"});
    model.study("std1").feature("param").set("plistarr", new String[]{"range(0,0.1,3)"});
    model.study("std1").feature("param").set("punit", new String[]{""});

    model.sol("sol1").attach("std1");
    model.sol("sol1").feature("st1").label("\u7f16\u8bd1\u65b9\u7a0b: \u7279\u5f81\u9891\u7387");
    model.sol("sol1").feature("v1").label("\u56e0\u53d8\u91cf 1.1");
    model.sol("sol1").feature("e1").label("\u7279\u5f81\u503c\u6c42\u89e3\u5668 1.1");
    model.sol("sol1").feature("e1").set("transform", "eigenfrequency");
    model.sol("sol1").feature("e1").set("shift", "100[Hz]");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").feature("dDef").label("\u76f4\u63a5 1");
    model.sol("sol1").feature("e1").feature("aDef").label("\u9ad8\u7ea7 1");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").runAll();

    model.batch("p1").set("control", "param");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"range(0,0.1,3)"});
    model.batch("p1").set("punit", new String[]{""});
    model.batch("p1").set("err", true);
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").feature("so1")
         .set("param", new String[]{"\"k\",\"0\"", "\"k\",\"0.1\"", "\"k\",\"0.2\"", "\"k\",\"0.3\"", "\"k\",\"0.4\"", "\"k\",\"0.5\"", "\"k\",\"0.6\"", "\"k\",\"0.7\"", "\"k\",\"0.8\"", "\"k\",\"0.9\"", 
         "\"k\",\"1\"", "\"k\",\"1.1\"", "\"k\",\"1.2\"", "\"k\",\"1.3\"", "\"k\",\"1.4\"", "\"k\",\"1.5\"", "\"k\",\"1.6\"", "\"k\",\"1.7\"", "\"k\",\"1.8\"", "\"k\",\"1.9\"", 
         "\"k\",\"2\"", "\"k\",\"2.1\"", "\"k\",\"2.2\"", "\"k\",\"2.3\"", "\"k\",\"2.4\"", "\"k\",\"2.5\"", "\"k\",\"2.6\"", "\"k\",\"2.7\"", "\"k\",\"2.8\"", "\"k\",\"2.9\"", 
         "\"k\",\"3\""});
    model.batch("p1").attach("std1");
    model.batch("p1").run();

    model.result().evaluationGroup("std1EvgFrq").label("\u7279\u5f81\u9891\u7387 (\u7814\u7a76 1)");
    model.result().evaluationGroup("std1EvgFrq").set("data", "dset2");
    model.result().evaluationGroup("std1EvgFrq").set("looplevelinput", new String[]{"all", "all"});
    model.result().evaluationGroup("std1EvgFrq").feature("gev1")
         .set("expr", new String[]{"freq*2*pi", "imag(freq)/abs(freq)", "abs(freq)/imag(freq)/2"});
    model.result().evaluationGroup("std1EvgFrq").feature("gev1").set("unit", new String[]{"rad/s", "1", "1"});
    model.result().evaluationGroup("std1EvgFrq").feature("gev1")
         .set("descr", new String[]{"\u89d2\u9891\u7387", "\u963b\u5c3c\u6bd4", "\u54c1\u8d28\u56e0\u5b50"});
    model.result().evaluationGroup("std1EvgFrq").run();
    model.result("pg1").label("\u58f0\u538b (acpr)");
    model.result("pg1").set("looplevel", new int[]{1, 21});
    model.result("pg1").set("showlegendsunit", true);
    model.result("pg1").feature("surf1").set("colorscalemode", "linearsymmetric");
    model.result("pg1").feature("surf1").set("resolution", "normal");
    model.result("pg1").feature("con1").set("colortable", "Wave");
    model.result("pg1").feature("con1").set("colorlegend", false);
    model.result("pg1").feature("con1").set("colorscalemode", "linearsymmetric");
    model.result("pg1").feature("con1").set("resolution", "normal");
    model.result("pg2").label("\u58f0\u538b\u7ea7 (acpr)");
    model.result("pg2").set("showlegendsunit", true);
    model.result("pg2").feature("surf1").set("resolution", "normal");
    model.result("pg3").set("xlabel", "k");
    model.result("pg3").set("ylabel", "\u9891\u7387 (Hz)");
    model.result("pg3").set("xlabelactive", false);
    model.result("pg3").set("ylabelactive", false);
    model.result("pg3").feature("glob1").set("expr", new String[]{"freq"});
    model.result("pg3").feature("glob1").set("unit", new String[]{"Hz"});
    model.result("pg3").feature("glob1").set("descr", new String[]{"\u9891\u7387"});
    model.result("pg3").feature("glob1").set("xdatasolnumtype", "outer");
    model.result("pg3").feature("glob1").set("linewidth", "preference");

    model.component("comp1").geom("geom1").run("fin");
    model.component("comp1").geom("geom1").feature().duplicate("mir2", "mir1");
    model.component("comp1").geom("geom1").feature().move("mir2", 3);
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature("mir1").selection("input").init();
    model.component("comp1").geom("geom1").feature("mir1").selection("input").set("pol2");
    model.component("comp1").geom("geom1").feature("mir1").selection("input").init();
    model.component("comp1").geom("geom1").feature("mir1").selection("input").set("pol1");
    model.component("comp1").geom("geom1").run("mir1");
    model.component("comp1").geom("geom1").feature("mir2").selection("input").set("pol2");
    model.component("comp1").geom("geom1").run("mir2");
    model.component("comp1").geom("geom1").feature().duplicate("rot2", "rot1");
    model.component("comp1").geom("geom1").feature("rot2").selection("input").init();
    model.component("comp1").geom("geom1").feature("rot2").selection("input").set("mir1", "pol1");
    model.component("comp1").geom("geom1").run("rot2");
    model.component("comp1").geom("geom1").feature("rot1").selection("input").init();
    model.component("comp1").geom("geom1").feature("rot1").selection("input").set("mir2", "pol2");
    model.component("comp1").geom("geom1").run("rot1");
    model.component("comp1").geom("geom1").feature("dif1").selection("input").init();
    model.component("comp1").geom("geom1").feature("dif1").selection("input").set("mir1", "pol1", "rot2");
    model.component("comp1").geom("geom1").feature("dif1").selection("input2").init();
    model.component("comp1").geom("geom1").feature("dif1").selection("input2").set("mir2", "pol2", "rot1");
    model.component("comp1").geom("geom1").run("dif1");

    model.label("ATI.mph");

    model.component("comp1").geom("geom1").run();

    model.component("comp1").physics("acpr").feature("pc1").selection().set(2, 24);
    model.component("comp1").physics("acpr").feature("pc2").selection().set();
    model.component("comp1").physics("acpr").feature("pc1").selection().set(3, 20);
    model.component("comp1").physics("acpr").feature("pc2").selection().set(2, 24);
    model.component("comp1").physics("acpr").feature("pc3").selection().set(9, 11);

    model.label("ATI2.mph");

    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature("pol2").remove("tableconstr", 2);
    model.component("comp1").geom("geom1").feature("pol2").remove("table", new int[]{2});
    model.component("comp1").geom("geom1").run("fin");

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"range(0,0.1,3)"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg3").run();
    model.result("pg1").run();
    model.result().table("evl2")
         .addRow(new double[]{4.837984085083008, -9.934680938720703, 0.300206900646277}, new double[]{0, 0, 0});
    model.result().table("evl2")
         .addRow(new double[]{6.157432556152344, -9.650093078613281, 0.1822397999452448}, new double[]{0, 0, 0});
    model.result("pg3").run();
    model.result("pg3").create("ptgr1", "PointGraph");
    model.result("pg3").feature("ptgr1").set("markerpos", "datapoints");
    model.result("pg3").feature("ptgr1").set("linewidth", "preference");
    model.result("pg3").feature("ptgr1").set("data", "dset2");
    model.result("pg3").feature("ptgr1").selection().all();
    model.result("pg3").feature("ptgr1").selection().set(6);
    model.result("pg3").feature("ptgr1").set("expr", "abs(acpr.p_t)");
    model.result("pg3").run();
    model.result("pg3").run();
    model.result("pg3").run();
    model.result("pg3").feature().remove("ptgr1");
    model.result("pg3").run();
    model.result().numerical().create("pev1", "EvalPoint");
    model.result().numerical("pev1").selection().set(10);
    model.result().numerical("pev1").set("expr", new String[]{"acpr.p_t"});
    model.result().numerical("pev1").set("descr", new String[]{"\u603b\u58f0\u538b"});
    model.result().numerical("pev1").set("unit", new String[]{"Pa"});
    model.result().numerical("pev1").setIndex("expr", "abs(acpr.p_t)", 0);
    model.result().table().create("tbl1", "Table");
    model.result().table("tbl1").comments("\u70b9\u8ba1\u7b97 1 {pev1}");
    model.result().numerical("pev1").set("table", "tbl1");
    model.result().numerical("pev1").setResult();
    model.result("pg1").run();
    model.result().table("evl2")
         .addRow(new double[]{6.079816818237305, -0.051743507385253906, 1.185235887351836}, new double[]{0, 0, 0});
    model.result().table("evl2")
         .addRow(new double[]{6.1833038330078125, -0.02587127685546875, 1.1854459010409}, new double[]{0, 0, 0});

    model.label("ATI2.mph");

    model.param().set("a", "21[mm]", "\u6676\u683c\u5e38\u6570");
    model.param().set("a", "42[mm]");
    model.param().descr("a", "\u6676\u683c\u5e38\u6570");
    model.param().set("rate1", "0.5");
    model.param().descr("rate1", "");
    model.param().set("rate2", "0.5");
    model.param().descr("rate2", "");
    model.param().set("k", "0");
    model.param().descr("k", "");
    model.param().set("rate3", "0.7");
    model.param().descr("rate3", "");
    model.param().set("rate4", "0.3");
    model.param().descr("rate4", "");
    model.param().set("d", "33[mm]");
    model.param().descr("d", "");

    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 0, 0, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 0, 0, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "-d*sqrt(3)/6", 1, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "d/2", 1, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "d*sqrt(3)/12", 2, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "d/4", 2, 1);
    model.component("comp1").geom("geom1").run("fin");

    model.study("std1").feature("param").setIndex("plistarr", 2, 0);

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");

    return model;
  }

  public static Model run2(Model model) {
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();

    model.component("comp1").geom("geom1").feature("pol2")
         .set("table", new String[][]{{"0", "0"}, {"-d*sqrt(3)/6", "d/2"}, {"d*sqrt(3)/12", "d/4"}, {"", ""}});
    model.component("comp1").geom("geom1").feature("pol2")
         .set("tableconstr", new String[]{"off", "off", "off", "off"});
    model.component("comp1").geom("geom1").feature("pol2").move("table", new int[]{3}, -1);
    model.component("comp1").geom("geom1").feature("pol2").move("tableconstr", new int[]{3}, -1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -11.5623, 2, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 20.8271, 2, 1);
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").run("fin");

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();

    model.label("ATI2.mph");

    model.result("pg1").run();

    model.param().remove(new String[]{"rate1", "rate2", "k", "rate3", "rate4", "d"});
    model.param().set("b_w", "6[mm]");
    model.param().descr("b_w", "\u81c2\u5bbd");
    model.param().set("d", "17[mm]");
    model.param().descr("d", "\u81c2\u957f");
    model.param().set("k", "2");
    model.param().descr("k", "");
    model.param().set("kx", "0");
    model.param().descr("kx", "");
    model.param().set("ky", "0");
    model.param().descr("ky", "");
    model.param().set("theta", "30");
    model.param().descr("theta", "");

    model.component("comp1").geom("geom1").run("pol1");
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature("pol2").remove("tableconstr", new int[]{0, 1, 2, 3});
    model.component("comp1").geom("geom1").feature("pol2").remove("table", new int[]{0, 1, 2, 3});
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "d", 0, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 0, 0, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "d", 1, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "b_w", 1, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "0.5*b_w", 0, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "-0.5*b_w", 1, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 0, 2, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "-0.5*b_w", 2, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 0, 3, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "0.5*b_w", 3, 1);
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").create("rot3", "Rotate");
    model.component("comp1").geom("geom1").feature("rot3").selection("input").set("pol2");
    model.component("comp1").geom("geom1").feature("rot3").set("rot", "theta");

    model.param().set("theta", "60");

    model.component("comp1").geom("geom1").run("rot3");
    model.component("comp1").geom("geom1").run("rot3");
    model.component("comp1").geom("geom1").create("int1", "Intersection");
    model.component("comp1").geom("geom1").feature("int1").selection("input").set("pol1", "rot3");
    model.component("comp1").geom("geom1").run("int1");
    model.component("comp1").geom("geom1").feature().duplicate("pol3", "pol2");
    model.component("comp1").geom("geom1").run("pol3");
    model.component("comp1").geom("geom1").feature().remove("pol3");
    model.component("comp1").geom("geom1").feature().duplicate("pol3", "pol1");
    model.component("comp1").geom("geom1").run("pol3");
    model.component("comp1").geom("geom1").feature("mir1").selection("input").set("int1", "pol3");
    model.component("comp1").geom("geom1").run("mir1");
    model.component("comp1").geom("geom1").feature().remove("mir2");
    model.component("comp1").geom("geom1").runPre("mir1");
    model.component("comp1").geom("geom1").run("mir1");
    model.component("comp1").geom("geom1").feature("rot2").selection("input").set("int1", "mir1", "pol3");
    model.component("comp1").geom("geom1").run("rot2");
    model.component("comp1").geom("geom1").feature().remove("rot1");
    model.component("comp1").geom("geom1").feature("dif1").selection("input").init();
    model.component("comp1").geom("geom1").feature("dif1").selection("input")
         .set("mir1(2)", "pol3", "rot2(5)", "rot2(6)", "rot2(7)", "rot2(8)");
    model.component("comp1").geom("geom1").feature("dif1").selection("input2")
         .set("int1", "mir1(1)", "rot2(1)", "rot2(2)", "rot2(3)", "rot2(4)");
    model.component("comp1").geom("geom1").run("dif1");
    model.component("comp1").geom("geom1").run();

    model.component("comp1").physics("acpr").feature("pc1").selection().set(3, 23);
    model.component("comp1").physics("acpr").feature("pc2").selection().set(2, 24);
    model.component("comp1").physics("acpr").feature("pc3").selection().set(9, 11);

    model.study("std1").feature("param").setIndex("pname", "a", 0);
    model.study("std1").feature("param").setIndex("plistarr", "", 0);
    model.study("std1").feature("param").setIndex("punit", "m", 0);
    model.study("std1").feature("param").setIndex("pname", "a", 0);
    model.study("std1").feature("param").setIndex("plistarr", "", 0);
    model.study("std1").feature("param").setIndex("punit", "m", 0);
    model.study("std1").feature("param").setIndex("pname", "k", 0);
    model.study("std1").feature("param").setIndex("plistarr", "range(0,0.1,3)", 0);

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"range(0,0.1,3)"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg3").run();
    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{2, 1});
    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{1, 1});
    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{2, 1});
    model.result("pg1").run();
    model.result().table("evl2")
         .addRow(new double[]{-1.2374992370605469, 8.043750762939453, 1.213833363277205}, new double[]{0, 0, 0});
    model.result().table("evl2")
         .addRow(new double[]{-1.2374992370605469, 8.043750762939453, 1.213833363277205}, new double[]{0, 0, 0});
    model.result("pg1").set("looplevel", new int[]{1, 1});
    model.result("pg1").run();
    model.result().table("evl2")
         .addRow(new double[]{-0.4125022888183594, 9.556251525878906, 0.9354730719519997}, new double[]{0, 0, 0});
    model.result().table("evl2")
         .addRow(new double[]{-0.8250007629394531, 7.974998474121094, 0.9820176214373995}, new double[]{0, 0, 0});
    model.result().table("evl2")
         .addRow(new double[]{-0.8937492370605469, 6.737499237060547, 1.0075805629810846}, new double[]{0, 0, 0});
    model.result().table("evl2")
         .addRow(new double[]{-0.13750076293945312, 7.837501525878906, 0.9733876445787172}, new double[]{0, 0, 0});
    model.result().table("evl2")
         .addRow(new double[]{-1.0312461853027344, 6.943748474121094, 1.0056382795890468}, new double[]{0, 0, 0});
    model.result().table("evl2")
         .addRow(new double[]{-11.481250762939453, 20.625, 1.1310036158139267}, new double[]{0, 0, 0});
    model.result("pg1").set("looplevel", new int[]{2, 1});
    model.result("pg1").run();
    model.result().table("evl2")
         .addRow(new double[]{-10.518749237060547, 19.799999237060547, 0.5183052404977717}, new double[]{0, 0, 0});
    model.result("pg1").stepNext(0);
    model.result("pg1").run();
    model.result("pg1").stepNext(0);
    model.result("pg1").run();
    model.result("pg1").stepPrevious(0);
    model.result("pg1").run();
    model.result("pg1").stepNext(0);
    model.result("pg1").run();
    model.result("pg1").stepNext(0);
    model.result("pg1").run();
    model.result("pg1").stepNext(0);
    model.result("pg1").run();
    model.result("pg3").run();

    model.label("ATI2.mph");

    model.result("pg3").run();

    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").run("rot3");

    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{1, 1});
    model.result("pg1").run();

    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").run("rot3");
    model.component("comp1").geom("geom1").run("int1");
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").run("rot3");
    model.component("comp1").geom("geom1").run("int1");
    model.component("comp1").geom("geom1").run("fin");
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").run("rot3");

    model.result("pg1").run();

    model.component("comp1").geom("geom1").run("rot3");
    model.component("comp1").geom("geom1").run("rot3");
    model.component("comp1").geom("geom1").run("int1");
    model.component("comp1").geom("geom1").run("rot3");

    model.label("ATI4.mph");

    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 0, 0, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 0, 0, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "d*cos(pi/3)", 1, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "d*sin(pi/3)", 1, 1);
    model.component("comp1").geom("geom1").feature("pol2").remove("tableconstr", new int[]{2, 3});
    model.component("comp1").geom("geom1").feature("pol2").remove("table", new int[]{2, 3});
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "d*cos(pi/3)-2", 2, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "d*sin(pi/3)-2", 2, 1);
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "d*cos(pi/3)-(0.5*b_w*cos(pi/6))", 2, 0);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "d*cos(pi/3)-(0.5*b_w*cos(pi/6))", 2, 1);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "d*cos(pi/3)+(0.5*b_w*sin(pi/6))", 2, 1);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "d*sin(pi/3)+(0.5*b_w*sin(pi/6))", 2, 1);
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "0-(0.5*b_w/sqrt(3)*2*cos(pi/3)_", 3, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "0-(0.5*b_w/sqrt(3)*2*cos(pi/3)", 3, 0);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "0-(0.5*b_w/sqrt(3)*2*cos(pi/3))", 3, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "0+", 3, 1);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "0+-(0.5*b_w/sqrt(3)*2*sin(pi/3))", 3, 1);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "0+(0.5*b_w/sqrt(3)*2*sin(pi/3))", 3, 1);
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature().remove("rot3");
    model.component("comp1").geom("geom1").feature().remove("int1");
    model.component("comp1").geom("geom1").feature().remove("pol3");
    model.component("comp1").geom("geom1").run("mir1");
    model.component("comp1").geom("geom1").runPre("mir1");
    model.component("comp1").geom("geom1").feature("mir1").selection("input").set("pol1", "pol2");
    model.component("comp1").geom("geom1").run("mir1");
    model.component("comp1").geom("geom1").feature("rot2").selection("input").set("mir1", "pol1", "pol2");
    model.component("comp1").geom("geom1").run("rot2");
    model.component("comp1").geom("geom1").feature("dif1").selection("input")
         .set("mir1(1)", "pol1", "rot2(1)", "rot2(2)", "rot2(5)", "rot2(6)");
    model.component("comp1").geom("geom1").run("dif1");
    model.component("comp1").geom("geom1").runPre("dif1");
    model.component("comp1").geom("geom1").feature("dif1").selection("input2").init();
    model.component("comp1").geom("geom1").feature("dif1").selection("input2").set("rot2(5)");
    model.component("comp1").geom("geom1").feature("dif1").selection("input2").init();
    model.component("comp1").geom("geom1").feature("dif1").selection("input2")
         .set("mir1(2)", "pol2", "rot2(3)", "rot2(4)", "rot2(7)", "rot2(8)");
    model.component("comp1").geom("geom1").run("dif1");
    model.component("comp1").geom("geom1").run();

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"range(0,0.1,3)"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg3").run();
    model.result("pg1").run();

    model.study("std1").feature("param").setIndex("plistarr", 2, 0);

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();

    model.label("ATI4.mph");

    model.result("pg1").run();

    model.component("comp1").geom("geom1").run("pol2");

    model.param().set("d", "10[mm]");

    model.component("comp1").geom("geom1").run("fin");

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{2, 1});
    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{1, 1});
    model.result("pg1").run();

    model.label("ATI4.mph");

    model.result("pg1").run();

    model.param().set("d", "17[mm]");

    model.component("comp1").geom("geom1").run("fin");

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();

    model.param().set("d", "20[mm]");

    model.component("comp1").geom("geom1").run("fin");

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");

    return model;
  }

  public static Model run3(Model model) {
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg3").run();
    model.result("pg1").run();

    model.label("ATI7.mph");

    model.result("pg1").run();

    model.component("comp1").geom("geom1").feature("pol2")
         .set("table", new String[][]{{"0", "0"}, {"d*cos(pi/3)", "d*sin(pi/3)"}, {"d*cos(pi/3)-(0.5*b_w*cos(pi/6))", "d*sin(pi/3)+(0.5*b_w*sin(pi/6))"}, {"0-(0.5*b_w/sqrt(3)*2*cos(pi/3))", "0+(0.5*b_w/sqrt(3)*2*sin(pi/3))"}, {"", ""}});
    model.component("comp1").geom("geom1").feature("pol2")
         .set("tableconstr", new String[]{"off", "off", "off", "off", "off"});
    model.component("comp1").geom("geom1").feature("pol2").move("table", new int[]{4}, -1);
    model.component("comp1").geom("geom1").feature("pol2").move("tableconstr", new int[]{4}, -1);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "d*cos(pi/3)-(0.5*b_w*cos(pi/6))", 3, 0);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "d*sin(pi/3)+(0.5*b_w*sin(pi/6))", 3, 1);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "(d-5)*cos(pi/3)-(0.5*b_w*cos(pi/6))", 3, 0);
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "(d-5)*sin(pi/3)+(0.5*b_w*sin(pi/6))", 3, 1);
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "(d-5)*cos(pi/3)-(0.5*b_w*cos(pi/6))+1", 3, 0);
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature("pol2")
         .setIndex("table", "(d-5)*cos(pi/3)-(0.5*b_w*cos(pi/6))+2", 3, 0);
    model.component("comp1").geom("geom1").run("pol2");

    model.result("pg1").run();

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();

    model.label("ATI7.mph");

    model.result("pg1").run();

    model.component("comp1").geom("geom1").feature("pol2").remove("tableconstr", 3);
    model.component("comp1").geom("geom1").feature("pol2").remove("table", new int[]{3});
    model.component("comp1").geom("geom1").run("fin");

    model.param().set("d", "10[mm]");

    model.component("comp1").geom("geom1").run("fin");

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg1").run();

    model.label("com.mph");

    model.result("pg1").run();

    model.component("comp1").geom("geom1").feature("pol1").remove("tableconstr", 0);
    model.component("comp1").geom("geom1").feature("pol1").remove("table", new int[]{0});
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "-a/sqrt(3)", 2, 0);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", 0, 2, 1);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "-a/(2*sqrt(3))", 3, 0);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "-a/2", 3, 1);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "a/(2*sqrt(3))", 4, 0);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "a/2", 4, 1);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "-a/sqrt(3)", 5, 0);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "-a/sqrt(3)", 5, 1);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "a/sqrt(3)", 5, 0);
    model.component("comp1").geom("geom1").run("pol1");
    model.component("comp1").geom("geom1").run("pol1");
    model.component("comp1").geom("geom1").feature("pol1").remove("tableconstr", 4);
    model.component("comp1").geom("geom1").feature("pol1").remove("table", new int[]{4});
    model.component("comp1").geom("geom1").run("pol1");
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "a/(2*sqrt(3))", 4, 0);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "-a/2", 4, 1);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", "a/sqrt(3)", 5, 0);
    model.component("comp1").geom("geom1").feature("pol1").setIndex("table", 0, 5, 1);
    model.component("comp1").geom("geom1").run("pol1");
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -1.73205080756888, 0, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "3.00000000000000", 0, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 2.40192378864669, 1, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 10.1602540378444, 1, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "5.00000000000000", 2, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 8.66025403784439, 2, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "5.00000000000000", 3, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 8.66025403784439, 3, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 7.59807621135332, 4, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 7.16025403784438, 4, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 3.46410161513776, 5, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "-1.33226762955019e-15", 5, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 3.46410161513776, 6, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "1.33226762955019e-15", 6, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 7.59807621135332, 7, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -7.16025403784438, 7, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "5.00000000000000", 8, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -8.66025403784439, 8, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "5.00000000000000", 9, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -8.66025403784439, 9, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", 2.40192378864668, 10, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -10.1602540378444, 10, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -1.73205080756888, 11, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "-3.00000000000000", 11, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -1.73205080756888, 12, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "-3.00000000000000", 12, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -10, 13, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "-3.00000000000000", 13, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -10, 14, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "-3.55271367880050e-15", 14, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -10, 15, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "-3.55271367880050e-15", 15, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -10, 16, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "3.00000000000000", 16, 1);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", -1.73205080756888, 17, 0);
    model.component("comp1").geom("geom1").feature("pol2").setIndex("table", "3.00000000000000", 17, 1);
    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").feature().remove("mir1");
    model.component("comp1").geom("geom1").feature().remove("rot2");
    model.component("comp1").geom("geom1").run("dif1");
    model.component("comp1").geom("geom1").run("fin");

    model.sol("sol1").study("std1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").attach("std1");

    model.component("comp1").physics("acpr").feature("pc1").selection().set(2, 17);
    model.component("comp1").physics("acpr").feature("pc2").selection().set(1, 18);
    model.component("comp1").physics("acpr").feature("pc3").selection().set(3, 4);

    model.component("comp1").mesh("mesh1").automatic(true);

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg3").run();
    model.result("pg1").run();

    model.label("com.mph");

    model.result("pg1").run();
    model.result("pg3").run();

    model.component("comp1").geom("geom1").run("pol2");
    model.component("comp1").geom("geom1").run("dif1");

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();

    model.component("comp1").physics("acpr").prop("MeshControl")
         .set("PhysicsControlledMeshMaximumFrequency", "10000[Hz]");

    model.component("comp1").mesh("mesh1").run();

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"2"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{2, 1});
    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{1, 1});
    model.result("pg1").run();

    model.label("com.mph");

    model.result("pg1").run();
    model.result("pg3").run();
    model.result("pg1").run();
    model.result("pg2").run();
    model.result("pg1").run();

    model.study("std1").feature("param").setIndex("plistarr", "range(0,0.3,3)", 0);

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"range(0,0.3,3)"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg3").run();

    model.study("std1").feature("param").setIndex("plistarr", "range(0,0.2,3)", 0);

    model.sol("sol1").study("std1");

    model.study("std1").feature("eig").set("notlistsolnum", 1);
    model.study("std1").feature("eig").set("notsolnum", "auto");
    model.study("std1").feature("eig").set("listsolnum", 1);
    model.study("std1").feature("eig").set("solnum", "auto");
    model.study("std1").feature("eig").set("linplistsolnum", new String[]{"1"});
    model.study("std1").feature("eig").set("linpsolnum", "auto");

    model.sol("sol1").feature().remove("e1");
    model.sol("sol1").feature().remove("v1");
    model.sol("sol1").feature().remove("st1");
    model.sol("sol1").create("st1", "StudyStep");
    model.sol("sol1").feature("st1").set("study", "std1");
    model.sol("sol1").feature("st1").set("studystep", "eig");
    model.sol("sol1").create("v1", "Variables");
    model.sol("sol1").feature("v1").set("control", "eig");
    model.sol("sol1").create("e1", "Eigenvalue");
    model.sol("sol1").feature("e1").set("neigs", 6);
    model.sol("sol1").feature("e1").set("shift", "0");
    model.sol("sol1").feature("e1").set("rtol", 1.0E-6);
    model.sol("sol1").feature("e1").set("transform", "none");
    model.sol("sol1").feature("e1").set("eigref", "100");
    model.sol("sol1").feature("e1").set("eigvfunscale", "average");
    model.sol("sol1").feature("e1").set("control", "eig");
    model.sol("sol1").feature("e1").feature("aDef").set("complexfun", true);
    model.sol("sol1").feature("e1").feature("aDef").set("cachepattern", false);
    model.sol("sol1").feature("e1").feature("aDef").set("matherr", true);
    model.sol("sol1").feature("e1").feature("aDef").set("blocksizeactive", false);
    model.sol("sol1").feature("e1").feature("aDef").set("nullfun", "auto");
    model.sol("sol1").attach("std1");

    model.batch("p1").feature().remove("so1");
    model.batch("p1").create("so1", "Solutionseq");
    model.batch("p1").feature("so1").set("seq", "sol1");
    model.batch("p1").feature("so1").set("store", "on");
    model.batch("p1").feature("so1").set("clear", "on");
    model.batch("p1").feature("so1").set("psol", "sol2");
    model.batch("p1").set("pname", new String[]{"k"});
    model.batch("p1").set("plistarr", new String[]{"range(0,0.2,3)"});
    model.batch("p1").set("sweeptype", "sparse");
    model.batch("p1").set("probesel", "all");
    model.batch("p1").set("probes", new String[]{});
    model.batch("p1").set("plot", "off");
    model.batch("p1").set("err", "on");
    model.batch("p1").attach("std1");
    model.batch("p1").set("control", "param");
    model.batch("p1").run("compute");

    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{1, 11});
    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{2, 11});
    model.result("pg1").run();
    model.result("pg3").run();

    model.label("com.mph");

    model.result("pg1").run();
    model.result("pg3").run();
    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").feature().remove("con1");
    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").set("looplevel", new int[]{1, 11});

    model.label("com.mph");

    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").run();
    model.result("pg1").run();

    return model;
  }

  public static void main(String[] args) {
    Model model = run();
    model = run2(model);
    run3(model);
  }

}
