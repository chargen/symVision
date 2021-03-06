package ptrman.bpsolver.pattern;

import ptrman.bpsolver.NetworkHandles;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Grouping/CLustering algorithm as described in
 * Foundalis disertation chapter 8.3.2
 * Foundalis disertation chapter 8.3.3
 */
public class Grouping
{
    public static Set<Pattern> group(List<Pattern> exemplarsSet, float clusteringThreshold, NetworkHandles networkHandles, IMatchingUpdate matchingUpdateImplementation, FeaturePatternMatching featurePatternMatching)
    {
        Set<Pattern> patterns;
        Set<Pattern> knownExemplars;
        
        patterns = new LinkedHashSet<>();
        knownExemplars = new LinkedHashSet<>();
        
        for( Pattern iterationExemplarI : exemplarsSet )
        {
            if( !exemplarIsSimilarToAPattern(iterationExemplarI, patterns, clusteringThreshold, networkHandles, matchingUpdateImplementation, featurePatternMatching) )
            {
                float maxSimilarity;
                Pattern closest;
                
                maxSimilarity = 0.0f;
                closest = null;
                
                for( Pattern iterationExemplarJ : knownExemplars )
                {
                    float similarity;
                    
                    similarity = match(iterationExemplarJ, iterationExemplarI, matchingUpdateImplementation, networkHandles, featurePatternMatching);
                    
                    if( similarity > maxSimilarity )
                    {
                        maxSimilarity = similarity;
                        closest = iterationExemplarJ;
                    }
                }
                
                if( maxSimilarity > clusteringThreshold )
                {
                    Pattern createdPattern;
                    
                    createdPattern = formPattern(closest, iterationExemplarI, networkHandles, matchingUpdateImplementation, featurePatternMatching);
                    
                    if( !resemblesOneOf(createdPattern, patterns, clusteringThreshold, matchingUpdateImplementation, networkHandles, featurePatternMatching) )
                    {
                        patterns.add(createdPattern);
                        knownExemplars.remove(closest);
                    }
                }
            }
            
            knownExemplars.add(iterationExemplarI);
        }
        
        // ASK< why UNION, is this wrong? >
        // does the union
        patterns.addAll(knownExemplars);
        return patterns;
    }
    
    private static boolean exemplarIsSimilarToAPattern(Pattern exemplar, Set<Pattern> patterns, float clusteringThreshold, NetworkHandles networkHandles, IMatchingUpdate matchingUpdateImplementation, FeaturePatternMatching featurePatternMatching)
    {
        float maxSimilarity;
        Pattern closestPattern;
        
        if( patterns.isEmpty() )
        {
            return false;
        }
        // else here
        
        maxSimilarity = 0.0f;
        closestPattern = null; // null means there is none
        
        for( Pattern iterationPattern : patterns )
        {
            if( resembles(iterationPattern, exemplar, clusteringThreshold, matchingUpdateImplementation, networkHandles, featurePatternMatching) )
            {
                float similarity;
                
                similarity = match(iterationPattern, exemplar, matchingUpdateImplementation, networkHandles, featurePatternMatching);
                if( similarity > maxSimilarity )
                {
                    maxSimilarity = similarity;
                    closestPattern = iterationPattern;
                }
            }
            
            if( closestPattern != null )
            {
                update(closestPattern, exemplar, networkHandles, matchingUpdateImplementation, featurePatternMatching);
            }
        }
        
        return false;
    }
    
    private static void update(Pattern orginal, Pattern additional, NetworkHandles networkHandles, IMatchingUpdate matchingUpdateImplementation, FeaturePatternMatching featurePatternMatching)
    {
        Pattern resultPattern;
        
        resultPattern = updateCore(orginal, additional, networkHandles, matchingUpdateImplementation, featurePatternMatching);
        orginal.exemplars = resultPattern.exemplars;
    }
    
    private static Pattern formPattern(Pattern a, Pattern b, NetworkHandles networkHandles, IMatchingUpdate matchingUpdateImplementation, FeaturePatternMatching featurePatternMatching)
    {
        return updateCore(a, b, networkHandles, matchingUpdateImplementation, featurePatternMatching);
    }
    
    /**
     * adds the pattern additional to orginal as described in
     * foundalis disertation chapter 8.3.3
     * returns a deep copy of the updated pattern
     * 
     * update and formPattern are the same algorithm
     */
    private static Pattern updateCore(Pattern orginal, Pattern additional, NetworkHandles networkHandles, IMatchingUpdate matchingUpdateImplementation, FeaturePatternMatching featurePatternMatching)
    {
        return matchingUpdateImplementation.updateCore(orginal, additional, networkHandles, featurePatternMatching);
    }
    
    private static boolean resemblesOneOf(Pattern a, Set<Pattern> set, float clusteringThreshold, IMatchingUpdate matchingUpdateImplementation, NetworkHandles networkHandles, FeaturePatternMatching featurePatternMatching)
    {
        for( Pattern iterationPattern : set )
        {
            if( resembles(a, iterationPattern, clusteringThreshold, matchingUpdateImplementation, networkHandles, featurePatternMatching) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean resembles(Pattern a, Pattern b, float clusteringThreshold, IMatchingUpdate matchingUpdateImplementation, NetworkHandles networkHandles, FeaturePatternMatching featurePatternMatching)
    {
        return match(a, b, matchingUpdateImplementation, networkHandles, featurePatternMatching) > clusteringThreshold;
    }
    
    private static float match(Pattern a, Pattern b, IMatchingUpdate matchingUpdateImplementation, NetworkHandles networkHandles, FeaturePatternMatching featurePatternMatching)
    {
        return matchingUpdateImplementation.match(a, b, networkHandles, featurePatternMatching);
    }
}
