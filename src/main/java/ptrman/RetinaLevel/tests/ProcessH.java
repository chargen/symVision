package ptrman.RetinaLevel.tests;

import ptrman.Datastructures.Vector2d;
import ptrman.RetinaLevel.RetinaPrimitive;
import ptrman.RetinaLevel.SingleLineDetector;
import java.util.ArrayList;
import ptrman.misc.Assert;

/**
 *
 * 
 */
public class ProcessH
{
    public static void main(String[] p)
    {
        test();
    }
    
    public static void test()
    {
        testOverlapA();
        testOverlapAEqual();
    }
    
    private static void testOverlapA()
    {
        ArrayList<RetinaPrimitive> detectors;
        ptrman.RetinaLevel.ProcessH processH = new ptrman.RetinaLevel.ProcessH();
        
        detectors = new ArrayList<>();
        detectors.add(RetinaPrimitive.makeLine(SingleLineDetector.createFromFloatPositions(new Vector2d<>(7.0f, 5.0f+2.0f*7.0f), new Vector2d<>(15.0f, 5.0f+2.0f*15.0f))));
        detectors.add(RetinaPrimitive.makeLine(SingleLineDetector.createFromFloatPositions(new Vector2d<>(5.0f, 5.0f+2.0f*5.0f+2.0f), new Vector2d<>(10.0f, 5.0f+2.0f*10.0f+2.0f))));
        
        
        processH.process(detectors);
        
        Assert.Assert(detectors.size() == 1, "");
        
        // TODO< test values for >
        // new Vector2d<>(5.0f, 5.0f+2.0f*5.0f+2.0f)
        // new Vector2d<>(15.0f, 5.0f+2.0f*15.0f)
    }
    
    private static void testOverlapAEqual()
    {
        ArrayList<RetinaPrimitive> detectors;
        ptrman.RetinaLevel.ProcessH processH = new ptrman.RetinaLevel.ProcessH();
        
        detectors = new ArrayList<>();
        detectors.add(RetinaPrimitive.makeLine(SingleLineDetector.createFromFloatPositions(new Vector2d<>(5.0f, 5.0f+2.0f*5.0f), new Vector2d<>(15.0f, 5.0f+2.0f*15.0f))));
        detectors.add(RetinaPrimitive.makeLine(SingleLineDetector.createFromFloatPositions(new Vector2d<>(5.0f, 5.0f+2.0f*5.0f+2.0f), new Vector2d<>(10.0f, 5.0f+2.0f*10.0f+2.0f))));
        
        
        processH.process(detectors);
        
        Assert.Assert(detectors.size() == 1, "");
    }
}