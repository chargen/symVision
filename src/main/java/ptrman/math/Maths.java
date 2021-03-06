package ptrman.math;

import com.gs.collections.api.list.primitive.IntList;
import com.gs.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.*;

public class Maths {
    public static float weightFloats(final float valueA, final float weightA, final float valueB, final float weightB) {
        return (valueA*weightA + valueB*weightB)/(weightA + weightB);
    }

    public static double weightDoubles(final double valueA, final double weightA, final double valueB, final double weightB) {
        return (valueA*weightA + valueB*weightB)/(weightA + weightB);
    }
    
    public static double power2(double x)
    {
        return x*x;
    }

    // SUPERCOMPILATION candidate
    public static double squaredDistance(double[] data)
    {
        double result;
        int i;

        // we play supercompiler
        // SUPERCOMPILATION remove this when we use supercompilation
        if( data.length == 2 )
        {
            return power2(data[0]) + power2(data[1]);
        }

        result = 0.0;

        for( i = 0; i < data.length; i++ )
        {
            result += power2(data[i]);
        }

        return result;
    }
    
    public static int faculty(int value)
    {
        int result, i;
        
        result = 1;
        
        for( i = 1; i < value; i++ )
        {
            result *= i;
        }
        
        return result;
    }

    public static float clamp01(float value)
    {
        return java.lang.Math.min(1.0f, java.lang.Math.max(value, 0.0f));
    }

    public static int clampInt(final int value, final int min, final int max) {
        return java.lang.Math.min(max, java.lang.Math.max(value, min));
    }

    public static int modNegativeWraparound(final int value, final int max) {
        if( value >= 0 ) {
            return value % max;
        }
        else {
            final int positiveValue = -value;
            final int positiveValueMod = positiveValue % max;
            return (max - positiveValueMod) % max;
        }
    }

    // can be very slow
    public static List<Integer> getRandomIndices(final int max, final int numberOfSamples, Random random) {
        List<Integer> candidates = new ArrayList<>();
        List<Integer> result = new ArrayList<>();

        for( int i = 0; i < max; i++ ) {
            candidates.add(i);
        }

        for( int sampleNumber = 0; sampleNumber < numberOfSamples; sampleNumber++ ) {
            final int candidate = random.nextInt(candidates.size());

            final int chosenIndex = candidates.get(candidate);
            candidates.remove(candidate);

            result.add(chosenIndex);
        }

        return result;
    }

    public static IntList getRandomElements(final IntList source, final int numberOfSamples, Random random) {
        if (source.size() <= numberOfSamples)
            return source;

        IntHashSet result = new IntHashSet();

        while( result.size() < numberOfSamples ) {
            final int sampleIndex = random.nextInt(source.size());
            result.add(source.get(sampleIndex));
        }

        return result.toList();
    }

    public static<Type> List<Type> getRandomElements(final List<Type> source, final int numberOfSamples, Random random) {
        if (source.size() <= numberOfSamples)
            return source;

        Set<Type> result = new TreeSet<>();

//        for( int i = 0; i < numberOfSamples; i++ ) {
//            final int sampleIndex = random.nextInt(source.size());
//            result.add(source.get(sampleIndex));
//        }

        while( result.size() < numberOfSamples ) {
            final int sampleIndex = random.nextInt(source.size());
            result.add(source.get(sampleIndex));
        }

        List<Type> resultList = new ArrayList<>(result);
        return resultList;
    }

    public static int nextPowerOfTwo(final int value) {
        int workingValue = value;

        int bitsFromLeft;
        for( bitsFromLeft = 0; bitsFromLeft < 32; bitsFromLeft++ ) {
            if( (value & (1 << (32-bitsFromLeft))) != 0 ) {
                break;
            }
        }

        int result;

        if( bitsFromLeft == 0 ) {
            result = 1 << 31;

            return result;
        }
        else {
            result = 1 << (31+1+1-bitsFromLeft);

            return result;
        }
    }

    public static boolean equals(final double a, final double b, final double epsilon) {
        if (a >= b) {
            return (a - b <= epsilon);
        }
        else //if (b > a) {
            return (b - a <= epsilon);
    }

}
