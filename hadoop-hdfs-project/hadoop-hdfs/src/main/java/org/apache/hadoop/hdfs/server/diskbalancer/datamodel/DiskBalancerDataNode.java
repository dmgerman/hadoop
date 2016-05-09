begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.datamodel
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
operator|.
name|datamodel
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * DiskBalancerDataNode represents a DataNode that exists in the cluster. It  * also contains a metric called nodeDataDensity which allows us to compare  * between a set of Nodes.  */
end_comment

begin_class
DECL|class|DiskBalancerDataNode
specifier|public
class|class
name|DiskBalancerDataNode
implements|implements
name|Comparable
argument_list|<
name|DiskBalancerDataNode
argument_list|>
block|{
DECL|field|nodeDataDensity
specifier|private
name|double
name|nodeDataDensity
decl_stmt|;
DECL|field|volumeSets
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DiskBalancerVolumeSet
argument_list|>
name|volumeSets
decl_stmt|;
DECL|field|dataNodeUUID
specifier|private
name|String
name|dataNodeUUID
decl_stmt|;
DECL|field|dataNodeIP
specifier|private
name|String
name|dataNodeIP
decl_stmt|;
DECL|field|dataNodePort
specifier|private
name|int
name|dataNodePort
decl_stmt|;
DECL|field|dataNodeName
specifier|private
name|String
name|dataNodeName
decl_stmt|;
DECL|field|volumeCount
specifier|private
name|int
name|volumeCount
decl_stmt|;
comment|/**    * Constructs an Empty Data Node.    */
DECL|method|DiskBalancerDataNode ()
specifier|public
name|DiskBalancerDataNode
parameter_list|()
block|{   }
comment|/**    * Constructs a DataNode.    *    * @param dataNodeID - Node ID    */
DECL|method|DiskBalancerDataNode (String dataNodeID)
specifier|public
name|DiskBalancerDataNode
parameter_list|(
name|String
name|dataNodeID
parameter_list|)
block|{
name|this
operator|.
name|dataNodeUUID
operator|=
name|dataNodeID
expr_stmt|;
name|volumeSets
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the IP address of this Node.    *    * @return IP Address string    */
DECL|method|getDataNodeIP ()
specifier|public
name|String
name|getDataNodeIP
parameter_list|()
block|{
return|return
name|dataNodeIP
return|;
block|}
comment|/**    * Sets the IP address of this Node.    *    * @param ipaddress - IP Address    */
DECL|method|setDataNodeIP (String ipaddress)
specifier|public
name|void
name|setDataNodeIP
parameter_list|(
name|String
name|ipaddress
parameter_list|)
block|{
name|this
operator|.
name|dataNodeIP
operator|=
name|ipaddress
expr_stmt|;
block|}
comment|/**    * Returns the Port of this DataNode.    *    * @return Port Number    */
DECL|method|getDataNodePort ()
specifier|public
name|int
name|getDataNodePort
parameter_list|()
block|{
return|return
name|dataNodePort
return|;
block|}
comment|/**    * Sets the DataNode Port number.    *    * @param port - Datanode Port Number    */
DECL|method|setDataNodePort (int port)
specifier|public
name|void
name|setDataNodePort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|dataNodePort
operator|=
name|port
expr_stmt|;
block|}
comment|/**    * Get DataNode DNS name.    *    * @return name of the node    */
DECL|method|getDataNodeName ()
specifier|public
name|String
name|getDataNodeName
parameter_list|()
block|{
return|return
name|dataNodeName
return|;
block|}
comment|/**    * Sets node's DNS name.    *    * @param name - Data node name    */
DECL|method|setDataNodeName (String name)
specifier|public
name|void
name|setDataNodeName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|dataNodeName
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * Returns the Volume sets on this node.    *    * @return a Map of VolumeSets    */
DECL|method|getVolumeSets ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|DiskBalancerVolumeSet
argument_list|>
name|getVolumeSets
parameter_list|()
block|{
return|return
name|volumeSets
return|;
block|}
comment|/**    * Returns datanode ID.    **/
DECL|method|getDataNodeUUID ()
specifier|public
name|String
name|getDataNodeUUID
parameter_list|()
block|{
return|return
name|dataNodeUUID
return|;
block|}
comment|/**    * Sets Datanode UUID.    *    * @param nodeID - Node ID.    */
DECL|method|setDataNodeUUID (String nodeID)
specifier|public
name|void
name|setDataNodeUUID
parameter_list|(
name|String
name|nodeID
parameter_list|)
block|{
name|this
operator|.
name|dataNodeUUID
operator|=
name|nodeID
expr_stmt|;
block|}
comment|/**    * Indicates whether some other object is "equal to" this one.    */
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DiskBalancerDataNode
name|that
init|=
operator|(
name|DiskBalancerDataNode
operator|)
name|obj
decl_stmt|;
return|return
name|dataNodeUUID
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getDataNodeUUID
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Compares this object with the specified object for order.  Returns a    * negative integer, zero, or a positive integer as this object is less than,    * equal to, or greater than the specified object.    *    * @param that the object to be compared.    * @return a negative integer, zero, or a positive integer as this object is    * less than, equal to, or greater than the specified object.    * @throws NullPointerException if the specified object is null    * @throws ClassCastException   if the specified object's type prevents it    *                              from being compared to this object.    */
annotation|@
name|Override
DECL|method|compareTo (DiskBalancerDataNode that)
specifier|public
name|int
name|compareTo
parameter_list|(
name|DiskBalancerDataNode
name|that
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|that
argument_list|)
expr_stmt|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|this
operator|.
name|nodeDataDensity
operator|-
name|that
operator|.
name|getNodeDataDensity
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|this
operator|.
name|nodeDataDensity
operator|-
name|that
operator|.
name|getNodeDataDensity
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|this
operator|.
name|nodeDataDensity
operator|-
name|that
operator|.
name|getNodeDataDensity
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
comment|/**    * Returns a hash code value for the object. This method is supported for the    * benefit of hash tables such as those provided by {@link HashMap}.    */
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * returns NodeDataDensity Metric.    *    * @return float    */
DECL|method|getNodeDataDensity ()
specifier|public
name|double
name|getNodeDataDensity
parameter_list|()
block|{
return|return
name|nodeDataDensity
return|;
block|}
comment|/**    * computes nodes data density.    *<p/>    * This metric allows us to compare different  nodes and how well the data is    * spread across a set of volumes inside the node.    */
DECL|method|computeNodeDensity ()
specifier|public
name|void
name|computeNodeDensity
parameter_list|()
block|{
name|double
name|sum
init|=
literal|0
decl_stmt|;
name|int
name|volcount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DiskBalancerVolumeSet
name|vset
range|:
name|volumeSets
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|DiskBalancerVolume
name|vol
range|:
name|vset
operator|.
name|getVolumes
argument_list|()
control|)
block|{
name|sum
operator|+=
name|Math
operator|.
name|abs
argument_list|(
name|vol
operator|.
name|getVolumeDataDensity
argument_list|()
argument_list|)
expr_stmt|;
name|volcount
operator|++
expr_stmt|;
block|}
block|}
name|nodeDataDensity
operator|=
name|sum
expr_stmt|;
name|this
operator|.
name|volumeCount
operator|=
name|volcount
expr_stmt|;
block|}
comment|/**    * Computes if this node needs balancing at all.    *    * @param threshold - Percentage    * @return true or false    */
DECL|method|isBalancingNeeded (double threshold)
specifier|public
name|boolean
name|isBalancingNeeded
parameter_list|(
name|double
name|threshold
parameter_list|)
block|{
for|for
control|(
name|DiskBalancerVolumeSet
name|vSet
range|:
name|getVolumeSets
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|vSet
operator|.
name|isBalancingNeeded
argument_list|(
name|threshold
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Adds a volume to the DataNode.    *<p/>    * it is assumed that we have one thread per node hence this call is not    * synchronised neither is the map is protected.    *    * @param volume - volume    */
DECL|method|addVolume (DiskBalancerVolume volume)
specifier|public
name|void
name|addVolume
parameter_list|(
name|DiskBalancerVolume
name|volume
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
argument_list|,
literal|"volume cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeSets
argument_list|,
literal|"volume sets cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
operator|.
name|getStorageType
argument_list|()
argument_list|,
literal|"storage type cannot be null"
argument_list|)
expr_stmt|;
name|String
name|volumeSetKey
init|=
name|volume
operator|.
name|getStorageType
argument_list|()
decl_stmt|;
name|DiskBalancerVolumeSet
name|vSet
decl_stmt|;
if|if
condition|(
name|volumeSets
operator|.
name|containsKey
argument_list|(
name|volumeSetKey
argument_list|)
condition|)
block|{
name|vSet
operator|=
name|volumeSets
operator|.
name|get
argument_list|(
name|volumeSetKey
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vSet
operator|=
operator|new
name|DiskBalancerVolumeSet
argument_list|(
name|volume
operator|.
name|isTransient
argument_list|()
argument_list|)
expr_stmt|;
name|vSet
operator|.
name|setStorageType
argument_list|(
name|volumeSetKey
argument_list|)
expr_stmt|;
name|volumeSets
operator|.
name|put
argument_list|(
name|volumeSetKey
argument_list|,
name|vSet
argument_list|)
expr_stmt|;
block|}
name|vSet
operator|.
name|addVolume
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|computeNodeDensity
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns how many volumes are in the DataNode.    *    * @return int    */
DECL|method|getVolumeCount ()
specifier|public
name|int
name|getVolumeCount
parameter_list|()
block|{
return|return
name|volumeCount
return|;
block|}
block|}
end_class

end_unit

