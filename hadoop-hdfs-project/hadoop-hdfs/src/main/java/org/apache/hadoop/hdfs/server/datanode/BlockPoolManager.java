begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|conf
operator|.
name|Configuration
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|DFSUtil
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

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
name|Joiner
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_comment
comment|/**  * Manages the BPOfferService objects for the data node.  * Creation, removal, starting, stopping, shutdown on BPOfferService  * objects must be done via APIs in this class.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockPoolManager
class|class
name|BlockPoolManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|DataNode
operator|.
name|LOG
decl_stmt|;
DECL|field|bpByNameserviceId
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BPOfferService
argument_list|>
name|bpByNameserviceId
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|bpByBlockPoolId
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BPOfferService
argument_list|>
name|bpByBlockPoolId
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|offerServices
specifier|private
specifier|final
name|List
argument_list|<
name|BPOfferService
argument_list|>
name|offerServices
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|dn
specifier|private
specifier|final
name|DataNode
name|dn
decl_stmt|;
comment|//This lock is used only to ensure exclusion of refreshNamenodes
DECL|field|refreshNamenodesLock
specifier|private
specifier|final
name|Object
name|refreshNamenodesLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|BlockPoolManager (DataNode dn)
name|BlockPoolManager
parameter_list|(
name|DataNode
name|dn
parameter_list|)
block|{
name|this
operator|.
name|dn
operator|=
name|dn
expr_stmt|;
block|}
DECL|method|addBlockPool (BPOfferService bpos)
specifier|synchronized
name|void
name|addBlockPool
parameter_list|(
name|BPOfferService
name|bpos
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|offerServices
operator|.
name|contains
argument_list|(
name|bpos
argument_list|)
argument_list|,
literal|"Unknown BPOS: %s"
argument_list|,
name|bpos
argument_list|)
expr_stmt|;
if|if
condition|(
name|bpos
operator|.
name|getBlockPoolId
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Null blockpool id"
argument_list|)
throw|;
block|}
name|bpByBlockPoolId
operator|.
name|put
argument_list|(
name|bpos
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|bpos
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the array of BPOfferService objects.     * Caution: The BPOfferService returned could be shutdown any time.    */
DECL|method|getAllNamenodeThreads ()
specifier|synchronized
name|BPOfferService
index|[]
name|getAllNamenodeThreads
parameter_list|()
block|{
name|BPOfferService
index|[]
name|bposArray
init|=
operator|new
name|BPOfferService
index|[
name|offerServices
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|offerServices
operator|.
name|toArray
argument_list|(
name|bposArray
argument_list|)
return|;
block|}
DECL|method|get (String bpid)
specifier|synchronized
name|BPOfferService
name|get
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
name|bpByBlockPoolId
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
return|;
block|}
DECL|method|remove (BPOfferService t)
specifier|synchronized
name|void
name|remove
parameter_list|(
name|BPOfferService
name|t
parameter_list|)
block|{
name|offerServices
operator|.
name|remove
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|bpByBlockPoolId
operator|.
name|remove
argument_list|(
name|t
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|removed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|BPOfferService
argument_list|>
name|it
init|=
name|bpByNameserviceId
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|removed
condition|;
control|)
block|{
name|BPOfferService
name|bpos
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|bpos
operator|==
name|t
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed "
operator|+
name|bpos
argument_list|)
expr_stmt|;
name|removed
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|removed
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't remove BPOS "
operator|+
name|t
operator|+
literal|" from bpByNameserviceId map"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shutDownAll ()
name|void
name|shutDownAll
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|BPOfferService
index|[]
name|bposArray
init|=
name|this
operator|.
name|getAllNamenodeThreads
argument_list|()
decl_stmt|;
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|bposArray
control|)
block|{
name|bpos
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|//interrupts the threads
block|}
comment|//now join
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|bposArray
control|)
block|{
name|bpos
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|startAll ()
specifier|synchronized
name|void
name|startAll
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|offerServices
control|)
block|{
name|bpos
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|()
decl_stmt|;
name|ioe
operator|.
name|initCause
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
DECL|method|joinAll ()
name|void
name|joinAll
parameter_list|()
block|{
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|this
operator|.
name|getAllNamenodeThreads
argument_list|()
control|)
block|{
name|bpos
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|refreshNamenodes (Configuration conf)
name|void
name|refreshNamenodes
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Refresh request received for nameservices: "
operator|+
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|InetSocketAddress
argument_list|>
argument_list|>
name|newAddressMap
init|=
name|DFSUtil
operator|.
name|getNNServiceRpcAddresses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|refreshNamenodesLock
init|)
block|{
name|doRefreshNamenodes
argument_list|(
name|newAddressMap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doRefreshNamenodes ( Map<String, Map<String, InetSocketAddress>> addrMap)
specifier|private
name|void
name|doRefreshNamenodes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|InetSocketAddress
argument_list|>
argument_list|>
name|addrMap
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|refreshNamenodesLock
argument_list|)
assert|;
name|Set
argument_list|<
name|String
argument_list|>
name|toRefresh
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|toAdd
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|toRemove
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Step 1. For each of the new nameservices, figure out whether
comment|// it's an update of the set of NNs for an existing NS,
comment|// or an entirely new nameservice.
for|for
control|(
name|String
name|nameserviceId
range|:
name|addrMap
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|bpByNameserviceId
operator|.
name|containsKey
argument_list|(
name|nameserviceId
argument_list|)
condition|)
block|{
name|toRefresh
operator|.
name|add
argument_list|(
name|nameserviceId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|toAdd
operator|.
name|add
argument_list|(
name|nameserviceId
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Step 2. Any nameservices we currently have but are no longer present
comment|// need to be removed.
name|toRemove
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|Sets
operator|.
name|difference
argument_list|(
name|bpByNameserviceId
operator|.
name|keySet
argument_list|()
argument_list|,
name|addrMap
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|toRefresh
operator|.
name|size
argument_list|()
operator|+
name|toAdd
operator|.
name|size
argument_list|()
operator|==
name|addrMap
operator|.
name|size
argument_list|()
operator|:
literal|"toAdd: "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|useForNull
argument_list|(
literal|"<default>"
argument_list|)
operator|.
name|join
argument_list|(
name|toAdd
argument_list|)
operator|+
literal|"  toRemove: "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|useForNull
argument_list|(
literal|"<default>"
argument_list|)
operator|.
name|join
argument_list|(
name|toRemove
argument_list|)
operator|+
literal|"  toRefresh: "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|useForNull
argument_list|(
literal|"<default>"
argument_list|)
operator|.
name|join
argument_list|(
name|toRefresh
argument_list|)
assert|;
comment|// Step 3. Start new nameservices
if|if
condition|(
operator|!
name|toAdd
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting BPOfferServices for nameservices: "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|useForNull
argument_list|(
literal|"<default>"
argument_list|)
operator|.
name|join
argument_list|(
name|toAdd
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nsToAdd
range|:
name|toAdd
control|)
block|{
name|ArrayList
argument_list|<
name|InetSocketAddress
argument_list|>
name|addrs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|addrMap
operator|.
name|get
argument_list|(
name|nsToAdd
argument_list|)
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|BPOfferService
name|bpos
init|=
name|createBPOS
argument_list|(
name|addrs
argument_list|)
decl_stmt|;
name|bpByNameserviceId
operator|.
name|put
argument_list|(
name|nsToAdd
argument_list|,
name|bpos
argument_list|)
expr_stmt|;
name|offerServices
operator|.
name|add
argument_list|(
name|bpos
argument_list|)
expr_stmt|;
block|}
block|}
name|startAll
argument_list|()
expr_stmt|;
block|}
comment|// Step 4. Shut down old nameservices. This happens outside
comment|// of the synchronized(this) lock since they need to call
comment|// back to .remove() from another thread
if|if
condition|(
operator|!
name|toRemove
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping BPOfferServices for nameservices: "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|useForNull
argument_list|(
literal|"<default>"
argument_list|)
operator|.
name|join
argument_list|(
name|toRemove
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nsToRemove
range|:
name|toRemove
control|)
block|{
name|BPOfferService
name|bpos
init|=
name|bpByNameserviceId
operator|.
name|get
argument_list|(
name|nsToRemove
argument_list|)
decl_stmt|;
name|bpos
operator|.
name|stop
argument_list|()
expr_stmt|;
name|bpos
operator|.
name|join
argument_list|()
expr_stmt|;
comment|// they will call remove on their own
block|}
block|}
comment|// Step 5. Update nameservices whose NN list has changed
if|if
condition|(
operator|!
name|toRefresh
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Refreshing list of NNs for nameservices: "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|useForNull
argument_list|(
literal|"<default>"
argument_list|)
operator|.
name|join
argument_list|(
name|toRefresh
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nsToRefresh
range|:
name|toRefresh
control|)
block|{
name|BPOfferService
name|bpos
init|=
name|bpByNameserviceId
operator|.
name|get
argument_list|(
name|nsToRefresh
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|InetSocketAddress
argument_list|>
name|addrs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|addrMap
operator|.
name|get
argument_list|(
name|nsToRefresh
argument_list|)
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|bpos
operator|.
name|refreshNNList
argument_list|(
name|addrs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Extracted out for test purposes.    */
DECL|method|createBPOS (List<InetSocketAddress> nnAddrs)
specifier|protected
name|BPOfferService
name|createBPOS
parameter_list|(
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|nnAddrs
parameter_list|)
block|{
return|return
operator|new
name|BPOfferService
argument_list|(
name|nnAddrs
argument_list|,
name|dn
argument_list|)
return|;
block|}
block|}
end_class

end_unit

