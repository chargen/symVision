package ptrman.bpsolver;

import org.apache.commons.math3.linear.ArrayRealVector;
import ptrman.FargGeneral.network.Link;
import ptrman.bpsolver.nodes.FeatureNode;
import ptrman.bpsolver.nodes.NodeTypes;
import ptrman.bpsolver.nodes.PlatonicPrimitiveInstanceNode;
import ptrman.bpsolver.nodes.PlatonicPrimitiveNode;

/**
 *
 * 
 */
public class HelperFunctions {
    public static PlatonicPrimitiveInstanceNode createVectorAttributeNode(ArrayRealVector vector, PlatonicPrimitiveNode primitiveNodeType, BpSolver bpSolver) {
        PlatonicPrimitiveInstanceNode createdVectorInstanceNode = new PlatonicPrimitiveInstanceNode(primitiveNodeType);
        
        final FeatureNode createdXNode = FeatureNode.createFloatNode(bpSolver.networkHandles.xCoordinatePlatonicPrimitiveNode, vector.getDataRef()[0], 1, bpSolver.platonicPrimitiveDatabase.getMaxValueByPrimitiveNode(bpSolver.networkHandles.xCoordinatePlatonicPrimitiveNode));
        final FeatureNode createdYNode = FeatureNode.createFloatNode(bpSolver.networkHandles.yCoordinatePlatonicPrimitiveNode, vector.getDataRef()[1], 1, bpSolver.platonicPrimitiveDatabase.getMaxValueByPrimitiveNode(bpSolver.networkHandles.yCoordinatePlatonicPrimitiveNode));
        final Link linkToXNode = bpSolver.network.linkCreator.createLink(Link.EnumType.HASATTRIBUTE, createdXNode);
        createdVectorInstanceNode.outgoingLinks.add(linkToXNode);
        final Link linkToYNode = bpSolver.network.linkCreator.createLink(Link.EnumType.HASATTRIBUTE, createdYNode);
        createdVectorInstanceNode.outgoingLinks.add(linkToYNode);
        
        return createdVectorInstanceNode;
    }
    
    public static ArrayRealVector getVectorFromVectorAttributeNode(final NetworkHandles networkHandles, final PlatonicPrimitiveInstanceNode node) {
        ArrayRealVector result = new ArrayRealVector(new double[]{0.0f, 0.0f});
        
        for( Link iterationLink : node.getLinksByType(Link.EnumType.HASATTRIBUTE) ) {
            FeatureNode targetFeatureNode;

            if( iterationLink.target.type != NodeTypes.EnumType.FEATURENODE.ordinal() ) {
                continue;
            }

            targetFeatureNode = (FeatureNode)iterationLink.target;

            if( targetFeatureNode.featureTypeNode.equals(networkHandles.xCoordinatePlatonicPrimitiveNode) ) {
                result.getDataRef()[0] = targetFeatureNode.getValueAsFloat();
            }
            else if( targetFeatureNode.featureTypeNode.equals(networkHandles.yCoordinatePlatonicPrimitiveNode) ) {
                result.getDataRef()[0] = targetFeatureNode.getValueAsFloat();
            }
            // else ignore
        }
        
        return result;
    }
}
