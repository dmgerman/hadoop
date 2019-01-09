begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringInterner
import|;
end_import

begin_comment
comment|/**  * Represents the network location of a block, information about the hosts  * that contain block replicas, and other block metadata (E.g. the file  * offset associated with the block, length, whether it is corrupt, etc).  *  * For a single BlockLocation, it will have different meanings for replicated  * and erasure coded files.  *  * If the file is 3-replicated, offset and length of a BlockLocation represent  * the absolute value in the file and the hosts are the 3 datanodes that  * holding the replicas. Here is an example:  *<pre>  * BlockLocation(offset: 0, length: BLOCK_SIZE,  *   hosts: {"host1:9866", "host2:9866, host3:9866"})  *</pre>  *  * And if the file is erasure-coded, each BlockLocation represents a logical  * block groups. Value offset is the offset of a block group in the file and  * value length is the total length of a block group. Hosts of a BlockLocation  * are the datanodes that holding all the data blocks and parity blocks of a  * block group.  * Suppose we have a RS_3_2 coded file (3 data units and 2 parity units).  * A BlockLocation example will be like:  *<pre>  * BlockLocation(offset: 0, length: 3 * BLOCK_SIZE, hosts: {"host1:9866",  *   "host2:9866","host3:9866","host4:9866","host5:9866"})  *</pre>  *  * Please refer to  * {@link FileSystem#getFileBlockLocations(FileStatus, long, long)} or  * {@link FileContext#getFileBlockLocations(Path, long, long)}  * for more examples.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|BlockLocation
specifier|public
class|class
name|BlockLocation
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0x22986f6d
decl_stmt|;
DECL|field|hosts
specifier|private
name|String
index|[]
name|hosts
decl_stmt|;
comment|// Datanode hostnames
DECL|field|cachedHosts
specifier|private
name|String
index|[]
name|cachedHosts
decl_stmt|;
comment|// Datanode hostnames with a cached replica
DECL|field|names
specifier|private
name|String
index|[]
name|names
decl_stmt|;
comment|// Datanode IP:xferPort for accessing the block
DECL|field|topologyPaths
specifier|private
name|String
index|[]
name|topologyPaths
decl_stmt|;
comment|// Full path name in network topology
DECL|field|storageIds
specifier|private
name|String
index|[]
name|storageIds
decl_stmt|;
comment|// Storage ID of each replica
DECL|field|storageTypes
specifier|private
name|StorageType
index|[]
name|storageTypes
decl_stmt|;
comment|// Storage type of each replica
DECL|field|offset
specifier|private
name|long
name|offset
decl_stmt|;
comment|// Offset of the block in the file
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|field|corrupt
specifier|private
name|boolean
name|corrupt
decl_stmt|;
DECL|field|EMPTY_STR_ARRAY
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|EMPTY_STR_ARRAY
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
DECL|field|EMPTY_STORAGE_TYPE_ARRAY
specifier|private
specifier|static
specifier|final
name|StorageType
index|[]
name|EMPTY_STORAGE_TYPE_ARRAY
init|=
operator|new
name|StorageType
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Default Constructor    */
DECL|method|BlockLocation ()
specifier|public
name|BlockLocation
parameter_list|()
block|{
name|this
argument_list|(
name|EMPTY_STR_ARRAY
argument_list|,
name|EMPTY_STR_ARRAY
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copy constructor    */
DECL|method|BlockLocation (BlockLocation that)
specifier|public
name|BlockLocation
parameter_list|(
name|BlockLocation
name|that
parameter_list|)
block|{
name|this
operator|.
name|hosts
operator|=
name|that
operator|.
name|hosts
expr_stmt|;
name|this
operator|.
name|cachedHosts
operator|=
name|that
operator|.
name|cachedHosts
expr_stmt|;
name|this
operator|.
name|names
operator|=
name|that
operator|.
name|names
expr_stmt|;
name|this
operator|.
name|topologyPaths
operator|=
name|that
operator|.
name|topologyPaths
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|that
operator|.
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|that
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|corrupt
operator|=
name|that
operator|.
name|corrupt
expr_stmt|;
name|this
operator|.
name|storageIds
operator|=
name|that
operator|.
name|storageIds
expr_stmt|;
name|this
operator|.
name|storageTypes
operator|=
name|that
operator|.
name|storageTypes
expr_stmt|;
block|}
comment|/**    * Constructor with host, name, offset and length    */
DECL|method|BlockLocation (String[] names, String[] hosts, long offset, long length)
specifier|public
name|BlockLocation
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
argument_list|(
name|names
argument_list|,
name|hosts
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor with host, name, offset, length and corrupt flag    */
DECL|method|BlockLocation (String[] names, String[] hosts, long offset, long length, boolean corrupt)
specifier|public
name|BlockLocation
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|corrupt
parameter_list|)
block|{
name|this
argument_list|(
name|names
argument_list|,
name|hosts
argument_list|,
literal|null
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|corrupt
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor with host, name, network topology, offset and length    */
DECL|method|BlockLocation (String[] names, String[] hosts, String[] topologyPaths, long offset, long length)
specifier|public
name|BlockLocation
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|,
name|String
index|[]
name|topologyPaths
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
argument_list|(
name|names
argument_list|,
name|hosts
argument_list|,
name|topologyPaths
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor with host, name, network topology, offset, length     * and corrupt flag    */
DECL|method|BlockLocation (String[] names, String[] hosts, String[] topologyPaths, long offset, long length, boolean corrupt)
specifier|public
name|BlockLocation
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|,
name|String
index|[]
name|topologyPaths
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|corrupt
parameter_list|)
block|{
name|this
argument_list|(
name|names
argument_list|,
name|hosts
argument_list|,
literal|null
argument_list|,
name|topologyPaths
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|corrupt
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockLocation (String[] names, String[] hosts, String[] cachedHosts, String[] topologyPaths, long offset, long length, boolean corrupt)
specifier|public
name|BlockLocation
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|,
name|String
index|[]
name|cachedHosts
parameter_list|,
name|String
index|[]
name|topologyPaths
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|corrupt
parameter_list|)
block|{
name|this
argument_list|(
name|names
argument_list|,
name|hosts
argument_list|,
name|cachedHosts
argument_list|,
name|topologyPaths
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|corrupt
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockLocation (String[] names, String[] hosts, String[] cachedHosts, String[] topologyPaths, String[] storageIds, StorageType[] storageTypes, long offset, long length, boolean corrupt)
specifier|public
name|BlockLocation
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|,
name|String
index|[]
name|cachedHosts
parameter_list|,
name|String
index|[]
name|topologyPaths
parameter_list|,
name|String
index|[]
name|storageIds
parameter_list|,
name|StorageType
index|[]
name|storageTypes
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|corrupt
parameter_list|)
block|{
if|if
condition|(
name|names
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|names
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|names
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hosts
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|hosts
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|hosts
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|hosts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cachedHosts
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|cachedHosts
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|cachedHosts
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|cachedHosts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|topologyPaths
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|topologyPaths
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|topologyPaths
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|topologyPaths
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storageIds
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|storageIds
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|storageIds
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|storageIds
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storageTypes
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|storageTypes
operator|=
name|EMPTY_STORAGE_TYPE_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|storageTypes
operator|=
name|storageTypes
expr_stmt|;
block|}
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|corrupt
operator|=
name|corrupt
expr_stmt|;
block|}
comment|/**    * Get the list of hosts (hostname) hosting this block    */
DECL|method|getHosts ()
specifier|public
name|String
index|[]
name|getHosts
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|hosts
return|;
block|}
comment|/**    * Get the list of hosts (hostname) hosting a cached replica of the block    */
DECL|method|getCachedHosts ()
specifier|public
name|String
index|[]
name|getCachedHosts
parameter_list|()
block|{
return|return
name|cachedHosts
return|;
block|}
comment|/**    * Get the list of names (IP:xferPort) hosting this block    */
DECL|method|getNames ()
specifier|public
name|String
index|[]
name|getNames
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|names
return|;
block|}
comment|/**    * Get the list of network topology paths for each of the hosts.    * The last component of the path is the "name" (IP:xferPort).    */
DECL|method|getTopologyPaths ()
specifier|public
name|String
index|[]
name|getTopologyPaths
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|topologyPaths
return|;
block|}
comment|/**    * Get the storageID of each replica of the block.    */
DECL|method|getStorageIds ()
specifier|public
name|String
index|[]
name|getStorageIds
parameter_list|()
block|{
return|return
name|storageIds
return|;
block|}
comment|/**    * Get the storage type of each replica of the block.    */
DECL|method|getStorageTypes ()
specifier|public
name|StorageType
index|[]
name|getStorageTypes
parameter_list|()
block|{
return|return
name|storageTypes
return|;
block|}
comment|/**    * Get the start offset of file associated with this block    */
DECL|method|getOffset ()
specifier|public
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
comment|/**    * Get the length of the block    */
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**    * Get the corrupt flag.    */
DECL|method|isCorrupt ()
specifier|public
name|boolean
name|isCorrupt
parameter_list|()
block|{
return|return
name|corrupt
return|;
block|}
comment|/**    * Return true if the block is striped (erasure coded).    */
DECL|method|isStriped ()
specifier|public
name|boolean
name|isStriped
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Set the start offset of file associated with this block    */
DECL|method|setOffset (long offset)
specifier|public
name|void
name|setOffset
parameter_list|(
name|long
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
comment|/**    * Set the length of block    */
DECL|method|setLength (long length)
specifier|public
name|void
name|setLength
parameter_list|(
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * Set the corrupt flag.    */
DECL|method|setCorrupt (boolean corrupt)
specifier|public
name|void
name|setCorrupt
parameter_list|(
name|boolean
name|corrupt
parameter_list|)
block|{
name|this
operator|.
name|corrupt
operator|=
name|corrupt
expr_stmt|;
block|}
comment|/**    * Set the hosts hosting this block    */
DECL|method|setHosts (String[] hosts)
specifier|public
name|void
name|setHosts
parameter_list|(
name|String
index|[]
name|hosts
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hosts
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|hosts
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|hosts
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|hosts
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the hosts hosting a cached replica of this block    */
DECL|method|setCachedHosts (String[] cachedHosts)
specifier|public
name|void
name|setCachedHosts
parameter_list|(
name|String
index|[]
name|cachedHosts
parameter_list|)
block|{
if|if
condition|(
name|cachedHosts
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|cachedHosts
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|cachedHosts
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|cachedHosts
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the names (host:port) hosting this block    */
DECL|method|setNames (String[] names)
specifier|public
name|void
name|setNames
parameter_list|(
name|String
index|[]
name|names
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|names
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|names
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|names
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the network topology paths of the hosts    */
DECL|method|setTopologyPaths (String[] topologyPaths)
specifier|public
name|void
name|setTopologyPaths
parameter_list|(
name|String
index|[]
name|topologyPaths
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|topologyPaths
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|topologyPaths
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|topologyPaths
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|topologyPaths
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setStorageIds (String[] storageIds)
specifier|public
name|void
name|setStorageIds
parameter_list|(
name|String
index|[]
name|storageIds
parameter_list|)
block|{
if|if
condition|(
name|storageIds
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|storageIds
operator|=
name|EMPTY_STR_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|storageIds
operator|=
name|StringInterner
operator|.
name|internStringsInArray
argument_list|(
name|storageIds
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setStorageTypes (StorageType[] storageTypes)
specifier|public
name|void
name|setStorageTypes
parameter_list|(
name|StorageType
index|[]
name|storageTypes
parameter_list|)
block|{
if|if
condition|(
name|storageTypes
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|storageTypes
operator|=
name|EMPTY_STORAGE_TYPE_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|storageTypes
operator|=
name|storageTypes
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|corrupt
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"(corrupt)"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|h
range|:
name|hosts
control|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|h
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

