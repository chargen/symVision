package ptrman.levels.retina;

import org.apache.commons.math3.linear.ArrayRealVector;
import ptrman.Datastructures.IMap2d;
import ptrman.Datastructures.SpatialAcceleratedMap2d;
import ptrman.Datastructures.Tuple2;
import ptrman.Datastructures.Vector2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ptrman.math.ArrayRealVectorHelper.arrayRealVectorToInteger;


/**
 *
 * calculates the altitude
 */
public class ProcessB {
    private int counterCellPositiveCandidates;
    private int counterCellCandidates;

    /**
     * 
     * we use the whole image, in phaeaco he worked with the incomplete image witht the guiding of processA, this is not implemented that way 
     */
    public void process(List<ProcessA.Sample> samples, IMap2d<Boolean> map) {
        Vector2d<Integer> foundPosition;
        
        final int MAXRADIUS = (int)Math.sqrt(100.0*100.0);

        final int GRIDSIZE_FOR_SPATIALACCELERATEDMAP2D = 8;

        counterCellPositiveCandidates = 0;
        counterCellCandidates = 0;


        this.map = map;

        spatialAcceleratedMap2d = new SpatialAcceleratedMap2d(map, GRIDSIZE_FOR_SPATIALACCELERATEDMAP2D);
        spatialAcceleratedMap2d.recalculateGridCellStateMap();
        
        for( ProcessA.Sample iterationSample : samples ) {
            Tuple2<Vector2d<Integer>, Double> nearestResult;
            
            nearestResult = findNearestPositionWhereMapIs(false, arrayRealVectorToInteger(iterationSample.position), map, MAXRADIUS);
            if( nearestResult == null ) {
                iterationSample.altitude = ((MAXRADIUS+1)*2)*((MAXRADIUS+1)*2);
                continue;
            }
            // else here
            
            iterationSample.altitude = nearestResult.e1;
        }

        System.out.println("cell acceleration (positive cases): " + Float.toString(((float)counterCellPositiveCandidates / (float)counterCellCandidates) * 100.0f) + "%" );


    }
    
    // TODO< move into external function >
    // TODO< provide a version which doesn't need a maxradius (we need only that version) >
    /**
     * 
     * \return null if no point could be found in the radius 
     */
    private Tuple2<Vector2d<Integer>, Double> findNearestPositionWhereMapIs(boolean value, Vector2d<Integer> position, IMap2d<Boolean> image, int radius) {
        final ArrayRealVector positionReal = ptrman.math.ArrayRealVectorHelper.integerToArrayRealVector(position);

        final Vector2d<Integer> gridCenterPosition = spatialAcceleratedMap2d.getGridPositionOfPosition(position);

        final int gridMaxSearchRadius = 2 + radius / spatialAcceleratedMap2d.getGridsize();

        // set this to int.max when the radius is not limited
        int radiusToScan = gridMaxSearchRadius;

        Vector2d<Integer> nearestPixelCandidate = null;
        double nearestPixelCandidateDistance = Double.MAX_VALUE;

        for( int currentGridRadius = 0; currentGridRadius < radiusToScan; currentGridRadius++ ) {
            final List<Vector2d<Integer>> gridCellsToScan;

            // if we are at the center we need to scan only the center
            if( currentGridRadius == 0 ) {
                gridCellsToScan = new ArrayList<Vector2d<Integer>>(Arrays.asList(new Vector2d[]{gridCenterPosition}));
            }
            else {
                gridCellsToScan = spatialAcceleratedMap2d.getGridLocationsOfGridRadius(gridCenterPosition, currentGridRadius);
            }

            counterCellCandidates += gridCellsToScan.size();

            // use acceleration map and filter out the gridcells we don't need to scan
            gridCellsToScan.removeIf(cellPosition -> !spatialAcceleratedMap2d.canValueBeFoundInCell(cellPosition, value));

            counterCellPositiveCandidates += gridCellsToScan.size();

            final List<Vector2d<Integer>> pixelPositionsToCheck = getPositionsOfCandidatePixelsOfCells(gridCellsToScan, value);

            // pixel scan logic

            if( !gridCellsToScan.isEmpty() ) {
                // do this because we need to scan the next radius too
                radiusToScan = java.lang.Math.min(radiusToScan, currentGridRadius+1+1);
            }

            for( final Vector2d<Integer> iterationPixelPosition : pixelPositionsToCheck ) {
                final ArrayRealVector iterationPixelPositionReal = ptrman.math.ArrayRealVectorHelper.integerToArrayRealVector(iterationPixelPosition);

                final double currentDistance = positionReal.getDistance(iterationPixelPositionReal);
                if( currentDistance < nearestPixelCandidateDistance ) {
                    nearestPixelCandidateDistance = currentDistance;
                    nearestPixelCandidate = iterationPixelPosition;
                }
            }
        }

        return new Tuple2<>(nearestPixelCandidate, nearestPixelCandidateDistance);
    }

    private List<Vector2d<Integer>> getPositionsOfCandidatePixelsOfCells(final List<Vector2d<Integer>> cellPositions, final boolean value) {
        List<Vector2d<Integer>> result = new ArrayList<>();

        for( final Vector2d<Integer> iterationCellPosition : cellPositions ) {
            result.addAll(getPositionsOfCandidatePixelsOfCell(iterationCellPosition, value));
        }

        return result;
    }

    private List<Vector2d<Integer>> getPositionsOfCandidatePixelsOfCell(final Vector2d<Integer> cellPosition, final boolean value) {
        List<Vector2d<Integer>> result = new ArrayList<>();

        final int gridsize = spatialAcceleratedMap2d.getGridsize();

        for( int y = cellPosition.y * gridsize; y < (cellPosition.y+1) * gridsize; y++ ) {
            for( int x = cellPosition.x * gridsize; x < (cellPosition.x+1) * gridsize; x++ ) {
                if( map.readAt(x, y) == value ) {
                    result.add(new Vector2d<>(x, y));
                }
            }
        }

        return result;
    }

    private SpatialAcceleratedMap2d spatialAcceleratedMap2d;
    private IMap2d<Boolean> map;
}
