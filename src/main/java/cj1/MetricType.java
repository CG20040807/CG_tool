package cj1;

public enum MetricType {
    POROSITY("孔隙度") {
        @Override
        public double getValue(WellRecord record) {
            return record.getPorosityPercent();
        }
    },
    SHALE_VOLUME("泥质含量") {
        @Override
        public double getValue(WellRecord record) {
            return record.getShaleVolumePercent();
        }
    },
    OIL_SATURATION("含油饱和度") {
        @Override
        public double getValue(WellRecord record) {
            return record.getOilSaturationPercent();
        }
    };

    private final String label;

    MetricType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public abstract double getValue(WellRecord record);
}
