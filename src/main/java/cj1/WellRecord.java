package cj1;

import java.util.Locale;

public class WellRecord {

    private final String rawLine;
    private final double depth;
    private final double cal;
    private final double dt;
    private final double gr;
    private final double lld;
    private final double lls;
    private final double msfl;
    private final double nphi;
    private final double rhob;
    private final double sp;

    private double porosity;
    private double shaleVolume;
    private double oilSaturation;

    public WellRecord(String rawLine, double[] values) {
        this.rawLine = rawLine;
        this.depth = values[0];
        this.cal = values[1];
        this.dt = values[2];
        this.gr = values[3];
        this.lld = values[4];
        this.lls = values[5];
        this.msfl = values[6];
        this.nphi = values[7];
        this.rhob = values[8];
        this.sp = values[9];
    }

    public String formatRawData() {
        return String.format(Locale.US, "%.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f",
            depth, cal, dt, gr, lld, lls, msfl, nphi, rhob, sp);
    }

    public String formatVerificationData() {
        return String.format(Locale.US, "%.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f",
            depth + 2, cal + 2, dt + 2, gr + 2, lld + 2, lls + 2, msfl + 2, nphi + 2, rhob + 2, sp + 2);
    }

    public String formatFullResult() {
        return String.format(Locale.US,
            "%.4f  %.4f  %.4f  %.4f  %.4f  %.4f  %.4f  %.4f  %.4f  %.4f  %.3f  %.3f  %.3f",
            depth, cal, dt, gr, lld, lls, msfl, nphi, rhob, sp,
            getPorosityPercent(), getShaleVolumePercent(), getOilSaturationPercent());
    }

    public String getRawLine() {
        return rawLine;
    }

    public double getDepth() {
        return depth;
    }

    public double getDt() {
        return dt;
    }

    public double getGr() {
        return gr;
    }

    public double getLld() {
        return lld;
    }

    public double getPorosity() {
        return porosity;
    }

    public double getShaleVolume() {
        return shaleVolume;
    }

    public double getOilSaturation() {
        return oilSaturation;
    }

    public double getPorosityPercent() {
        return porosity * 100;
    }

    public double getShaleVolumePercent() {
        return shaleVolume * 100;
    }

    public double getOilSaturationPercent() {
        return oilSaturation * 100;
    }

    public void setPorosity(double porosity) {
        this.porosity = porosity;
    }

    public void setShaleVolume(double shaleVolume) {
        this.shaleVolume = shaleVolume;
    }

    public void setOilSaturation(double oilSaturation) {
        this.oilSaturation = oilSaturation;
    }

    public boolean isGoodOilLayer() {
        return porosity >= 0.06 && shaleVolume <= 0.25 && oilSaturation >= 0.60;
    }
}
