package ptrman.levels.retina;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomAdaptor;
import ptrman.Datastructures.IMap2d;
import ptrman.Datastructures.Vector2d;
import ptrman.levels.retina.helper.ProcessConnector;
import ptrman.misc.Assert;

import java.awt.*;
import java.util.Random;

/**
 *
 * samples from the input image and puts the set pixels into a queue (is for now just a list)
 */
public class ProcessA implements IProcess {
    @Override
    public void setImageSize(Vector2d<Integer> imageSize) {
        Assert.Assert((imageSize.x % 4) == 0, "imageSize.x must be divisable by 4");
        Assert.Assert((imageSize.y % 4) == 0, "imageSize.y must be divisable by 4");
    }

    @Override
    public void setup() {

    }

    @Override
    public void preProcessData() {

    }

    /**
     *
     * avoids samping the same pixel by setting the sampled positions to false
     *
     *
     *
     */
    @Override
    public void processData() {
        processData(1f);
    }


    int cycle = 0;

    Random rng = new RandomAdaptor( new MersenneTwister() );

    public void processData(float throttle) {

        cycle++;


        for( int blockY = 0; blockY < workingImage.getLength()/4; blockY++ ) {
            for( int blockX = 0; blockX < workingImage.getWidth()/4; blockX++ ) {
                int hitCount = 0;

                if (throttle < 1f)
                    if (rng.nextInt(100) < throttle*100)
                        continue;

                for( int y = blockY*4; y < (blockY+1)*4; y++ ) {
                    for (int x = blockX; x < (blockX+1)*4; x++) {
                        if( sampleMaskAtPosition(x, y, MaskDetail0) ) {
                            if( workingImage.readAt(x, y) ) {
                                hitCount++;
                                workingImage.setAt(x, y, false);

                                final int objectId = idMap.readAt(x, y);
                                //Assert.Assert(objectId  != -1, "");
                                /*if( objectId != -1 ) {
                                    int d = 0;
                                }*/

                                addSampleToOutput(x, y, objectId);
                            }
                        }
                    }
                }

                if( hitCount == 8 ) {
                    continue;
                }

                // sample it a second time for nearly all of the missing pixels
                for( int y = blockY*4; y < (blockY+1)*4; y++ ) {
                    for (int x = blockX; x < (blockX+1)*4; x++) {
                        if( sampleMaskAtPosition(x, y, MaskDetail1) ) {
                            if( workingImage.readAt(x, y) ) {
                                hitCount++;
                                workingImage.setAt(x, y, false);

                                final int objectId = idMap.readAt(x, y);
                                //Assert.Assert(objectId  != -1, "");
                                /*if( objectId != -1 ) {
                                    int d = 0;
                                }*/

                                addSampleToOutput(x, y, objectId);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void postProcessData() {

    }

    public static class Sample {
        public Sample getClone() {
            Sample clone = new Sample(position);
            clone.altitude = this.altitude;
            clone.type = this.type;
            clone.objectId = this.objectId;

            return clone;
        }

        public void debugPlot(Graphics2D detectorImageGraphics) {

            if (isObjectIdValid()) {
                detectorImageGraphics.setColor(Color.GREEN);
            } else {
                detectorImageGraphics.setColor(Color.BLUE);
            }

            final double[] pos = position.getDataRef();
            int positionX = (int) pos[0];
            int positionY = (int) pos[1];

            detectorImageGraphics.fillRect(positionX, positionY, 1, 1);

            if (isAltitudeValid()) {
                detectorImageGraphics.setColor(Color.RED);
                final int a = (int)(altitude * 4);
                detectorImageGraphics.drawOval(positionX-a/2, positionY-a/2, a, a);
            }

        }

        public enum EnumType {
            ENDOSCELETON,
            EXOSCELETON
        }

        public Sample(double x, double y) {
            this(new ArrayRealVector(new double[] { x, y}, false));
        }
        public Sample(float x, float y) {
            this(new ArrayRealVector(new double[] { x, y}, false));
        }
        public Sample(ArrayRealVector position) {
            this.position = position;
        }
        
        public boolean isAltitudeValid() {
            return Double.isFinite(altitude);
        }

        public boolean isObjectIdValid() {
            return objectId != -1;
        }
        
        public final ArrayRealVector position;
        public double altitude = Double.NaN;
        public EnumType type;
        public int objectId = -1;
    }
    
    
    public void set(IMap2d<Boolean> image, IMap2d<Integer> idMap, ProcessConnector<Sample> outputSampleConnector) {
        workingImage = image.copy();
        this.idMap = idMap;

        this.outputSampleConnector = outputSampleConnector;
    }

    private void addSampleToOutput(final int x, final int y, final int objectId) {
        Sample addSample = new Sample(x, y);
        addSample.objectId = objectId;

        outputSampleConnector.add(addSample);
    }

    private static boolean sampleMaskAtPosition(int px, int py, boolean[] mask4by4) {
        int modX, modY;

        modX = px % 4;
        modY = py % 4;

        return mask4by4[modX + modY * 4];
    }

    private IMap2d<Boolean> workingImage;
    private IMap2d<Integer> idMap;
    private ProcessConnector<Sample> outputSampleConnector;

    private static final boolean[] MaskDetail0 =
            {
                    true, false, false, true,
                    false, true, true, false,
                    true, false, true, false,
                    false, true, false, true
            };

    private static final boolean[] MaskDetail1 =
            {
                    false, false, false, true,
                    true, false, false, true,
                    false, true, false, false,
                    false, false, true, false
            };


}
