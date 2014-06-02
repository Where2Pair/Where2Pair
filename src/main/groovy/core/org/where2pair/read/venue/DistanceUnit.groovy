package org.where2pair.read.venue

enum DistanceUnit {
    MILES(new MilesDistanceCalculator()),
    KM(new KmDistanceCalculator())

    DistanceCalculator distanceCalculator

    private DistanceUnit(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator
    }

    Distance distanceBetween(Coordinates c1, Coordinates c2) {
        distanceCalculator.distanceBetween(c1, c2)
    }

    static interface DistanceCalculator {
        Distance distanceBetween(Coordinates c1, Coordinates c2)
    }

    static class KmDistanceCalculator implements DistanceCalculator {
        @Override
        Distance distanceBetween(Coordinates c1, Coordinates c2) {
            new Distance(value: rawDistanceBetween(c1, c2), unit: KM)
        }

        double rawDistanceBetween(Coordinates c1, Coordinates c2) {
            double earthRadius = 6371
            double dLat = Math.toRadians(c1.lat - c2.lat)
            double dLng = Math.toRadians(c1.lng - c2.lng)
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(c1.lat)) * Math.cos(Math.toRadians(c2.lat)) *
                    Math.sin(dLng / 2) * Math.sin(dLng / 2)
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            double dist = earthRadius * c
            dist.doubleValue()
        }
    }

    static class MilesDistanceCalculator extends KmDistanceCalculator {
        @Override
        Distance distanceBetween(Coordinates c1, Coordinates c2) {
            new Distance(value: rawDistanceBetween(c1, c2), unit: MILES)
        }

        @Override
        double rawDistanceBetween(Coordinates c1, Coordinates c2) {
            super.rawDistanceBetween(c1, c2) * 0.62137
        }
    }
}

