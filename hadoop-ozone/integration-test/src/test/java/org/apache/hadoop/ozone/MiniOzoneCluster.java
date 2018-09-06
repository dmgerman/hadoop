begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
package|;
end_package

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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|StorageContainerManager
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
name|ozone
operator|.
name|client
operator|.
name|OzoneClient
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
name|ozone
operator|.
name|om
operator|.
name|OzoneManager
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
name|hdds
operator|.
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

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
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_comment
comment|/**  * Interface used for MiniOzoneClusters.  */
end_comment

begin_interface
DECL|interface|MiniOzoneCluster
specifier|public
interface|interface
name|MiniOzoneCluster
block|{
comment|/**    * Returns the Builder to construct MiniOzoneCluster.    *    * @param conf OzoneConfiguration    *    * @return MiniOzoneCluster builder    */
DECL|method|newBuilder (OzoneConfiguration conf)
specifier|static
name|Builder
name|newBuilder
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|MiniOzoneClusterImpl
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Returns the configuration object associated with the MiniOzoneCluster.    *    * @return Configuration    */
DECL|method|getConf ()
name|Configuration
name|getConf
parameter_list|()
function_decl|;
comment|/**    * Waits for the cluster to be ready, this call blocks till all the    * configured {@link HddsDatanodeService} registers with    * {@link StorageContainerManager}.    *    * @throws TimeoutException In case of timeout    * @throws InterruptedException In case of interrupt while waiting    */
DECL|method|waitForClusterToBeReady ()
name|void
name|waitForClusterToBeReady
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Waits/blocks till the cluster is out of chill mode.    *    * @throws TimeoutException TimeoutException In case of timeout    * @throws InterruptedException In case of interrupt while waiting    */
DECL|method|waitTobeOutOfChillMode ()
name|void
name|waitTobeOutOfChillMode
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Returns {@link StorageContainerManager} associated with this    * {@link MiniOzoneCluster} instance.    *    * @return {@link StorageContainerManager} instance    */
DECL|method|getStorageContainerManager ()
name|StorageContainerManager
name|getStorageContainerManager
parameter_list|()
function_decl|;
comment|/**    * Returns {@link OzoneManager} associated with this    * {@link MiniOzoneCluster} instance.    *    * @return {@link OzoneManager} instance    */
DECL|method|getOzoneManager ()
name|OzoneManager
name|getOzoneManager
parameter_list|()
function_decl|;
comment|/**    * Returns the list of {@link HddsDatanodeService} which are part of this    * {@link MiniOzoneCluster} instance.    *    * @return List of {@link HddsDatanodeService}    */
DECL|method|getHddsDatanodes ()
name|List
argument_list|<
name|HddsDatanodeService
argument_list|>
name|getHddsDatanodes
parameter_list|()
function_decl|;
comment|/**    * Returns an {@link OzoneClient} to access the {@link MiniOzoneCluster}.    *    * @return {@link OzoneClient}    * @throws IOException    */
DECL|method|getClient ()
name|OzoneClient
name|getClient
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an RPC based {@link OzoneClient} to access the    * {@link MiniOzoneCluster}.    *    * @return {@link OzoneClient}    * @throws IOException    */
DECL|method|getRpcClient ()
name|OzoneClient
name|getRpcClient
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an REST based {@link OzoneClient} to access the    * {@link MiniOzoneCluster}.    *    * @return {@link OzoneClient}    * @throws IOException    */
DECL|method|getRestClient ()
name|OzoneClient
name|getRestClient
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns StorageContainerLocationClient to communicate with    * {@link StorageContainerManager} associated with the MiniOzoneCluster.    *    * @return StorageContainerLocation Client    * @throws IOException    */
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|method|getStorageContainerLocationClient ()
name|getStorageContainerLocationClient
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Restarts StorageContainerManager instance.    *    * @throws IOException    */
DECL|method|restartStorageContainerManager ()
name|void
name|restartStorageContainerManager
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Restarts OzoneManager instance.    *    * @throws IOException    */
DECL|method|restartOzoneManager ()
name|void
name|restartOzoneManager
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Restart a particular HddsDatanode.    *    * @param i index of HddsDatanode in the MiniOzoneCluster    */
DECL|method|restartHddsDatanode (int i)
name|void
name|restartHddsDatanode
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
function_decl|;
comment|/**    * Restart a particular HddsDatanode.    *    * @param dn HddsDatanode in the MiniOzoneCluster    */
DECL|method|restartHddsDatanode (DatanodeDetails dn)
name|void
name|restartHddsDatanode
parameter_list|(
name|DatanodeDetails
name|dn
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
throws|,
name|IOException
function_decl|;
comment|/**    * Shutdown a particular HddsDatanode.    *    * @param i index of HddsDatanode in the MiniOzoneCluster    */
DECL|method|shutdownHddsDatanode (int i)
name|void
name|shutdownHddsDatanode
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
comment|/**    * Shutdown a particular HddsDatanode.    *    * @param dn HddsDatanode in the MiniOzoneCluster    */
DECL|method|shutdownHddsDatanode (DatanodeDetails dn)
name|void
name|shutdownHddsDatanode
parameter_list|(
name|DatanodeDetails
name|dn
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Shutdown the MiniOzoneCluster.    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
function_decl|;
comment|/**    * Builder class for MiniOzoneCluster.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"CheckStyle"
argument_list|)
DECL|class|Builder
specifier|abstract
class|class
name|Builder
block|{
DECL|field|DEFAULT_HB_INTERVAL_MS
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_HB_INTERVAL_MS
init|=
literal|1000
decl_stmt|;
DECL|field|DEFAULT_HB_PROCESSOR_INTERVAL_MS
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_HB_PROCESSOR_INTERVAL_MS
init|=
literal|100
decl_stmt|;
DECL|field|conf
specifier|protected
specifier|final
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|path
specifier|protected
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|clusterId
specifier|protected
name|String
name|clusterId
decl_stmt|;
DECL|field|enableTrace
specifier|protected
name|Optional
argument_list|<
name|Boolean
argument_list|>
name|enableTrace
init|=
name|Optional
operator|.
name|of
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|hbInterval
specifier|protected
name|Optional
argument_list|<
name|Integer
argument_list|>
name|hbInterval
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|field|hbProcessorInterval
specifier|protected
name|Optional
argument_list|<
name|Integer
argument_list|>
name|hbProcessorInterval
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|field|scmId
specifier|protected
name|Optional
argument_list|<
name|String
argument_list|>
name|scmId
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|field|omId
specifier|protected
name|Optional
argument_list|<
name|String
argument_list|>
name|omId
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|field|ozoneEnabled
specifier|protected
name|Boolean
name|ozoneEnabled
init|=
literal|true
decl_stmt|;
DECL|field|randomContainerPort
specifier|protected
name|Boolean
name|randomContainerPort
init|=
literal|true
decl_stmt|;
comment|// Use relative smaller number of handlers for testing
DECL|field|numOfOmHandlers
specifier|protected
name|int
name|numOfOmHandlers
init|=
literal|20
decl_stmt|;
DECL|field|numOfScmHandlers
specifier|protected
name|int
name|numOfScmHandlers
init|=
literal|20
decl_stmt|;
DECL|field|numOfDatanodes
specifier|protected
name|int
name|numOfDatanodes
init|=
literal|1
decl_stmt|;
DECL|method|Builder (OzoneConfiguration conf)
specifier|protected
name|Builder
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|clusterId
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|MiniOzoneClusterImpl
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-"
operator|+
name|clusterId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the cluster Id.      *      * @param id cluster Id      *      * @return MiniOzoneCluster.Builder      */
DECL|method|setClusterId (String id)
specifier|public
name|Builder
name|setClusterId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|clusterId
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the SCM id.      *      * @param id SCM Id      *      * @return MiniOzoneCluster.Builder      */
DECL|method|setScmId (String id)
specifier|public
name|Builder
name|setScmId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|scmId
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the OM id.      *      * @param id OM Id      *      * @return MiniOzoneCluster.Builder      */
DECL|method|setOmId (String id)
specifier|public
name|Builder
name|setOmId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|omId
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * If set to true container service will be started in a random port.      *      * @param randomPort enable random port      *      * @return MiniOzoneCluster.Builder      */
DECL|method|setRandomContainerPort (boolean randomPort)
specifier|public
name|Builder
name|setRandomContainerPort
parameter_list|(
name|boolean
name|randomPort
parameter_list|)
block|{
name|randomContainerPort
operator|=
name|randomPort
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the number of HddsDatanodes to be started as part of      * MiniOzoneCluster.      *      * @param val number of datanodes      *      * @return MiniOzoneCluster.Builder      */
DECL|method|setNumDatanodes (int val)
specifier|public
name|Builder
name|setNumDatanodes
parameter_list|(
name|int
name|val
parameter_list|)
block|{
name|numOfDatanodes
operator|=
name|val
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the number of HeartBeat Interval of Datanodes, the value should be      * in MilliSeconds.      *      * @param val HeartBeat interval in milliseconds      *      * @return MiniOzoneCluster.Builder      */
DECL|method|setHbInterval (int val)
specifier|public
name|Builder
name|setHbInterval
parameter_list|(
name|int
name|val
parameter_list|)
block|{
name|hbInterval
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|val
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the number of HeartBeat Processor Interval of Datanodes,      * the value should be in MilliSeconds.      *      * @param val HeartBeat Processor interval in milliseconds      *      * @return MiniOzoneCluster.Builder      */
DECL|method|setHbProcessorInterval (int val)
specifier|public
name|Builder
name|setHbProcessorInterval
parameter_list|(
name|int
name|val
parameter_list|)
block|{
name|hbProcessorInterval
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|val
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * When set to true, enables trace level logging.      *      * @param trace true or false      *      * @return MiniOzoneCluster.Builder      */
DECL|method|setTrace (Boolean trace)
specifier|public
name|Builder
name|setTrace
parameter_list|(
name|Boolean
name|trace
parameter_list|)
block|{
name|enableTrace
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|trace
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Modifies the configuration such that Ozone will be disabled.      *      * @return MiniOzoneCluster.Builder      */
DECL|method|disableOzone ()
specifier|public
name|Builder
name|disableOzone
parameter_list|()
block|{
name|ozoneEnabled
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs and returns MiniOzoneCluster.      *      * @return {@link MiniOzoneCluster}      *      * @throws IOException      */
DECL|method|build ()
specifier|public
specifier|abstract
name|MiniOzoneCluster
name|build
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_interface

end_unit

