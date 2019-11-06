begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
name|namenode
operator|.
name|ha
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_INPROGRESS_KEY
import|;
end_import

begin_comment
comment|/**  * Test in progress tailing with small txn id per call.  *  * The number of edits that needs to be tailed during  * bootstrapStandby can be large, but the number of edits  * that can be tailed using RPC call can be limited  * (configured by dfs.ha.tail-edits.qjm.rpc.max-txns).  * This is to test that even with small number of configured  * txnid, bootstrapStandby can still work. See HDFS-14806.  */
end_comment

begin_class
DECL|class|TestBootstrapStandbyWithInProgressTailing
specifier|public
class|class
name|TestBootstrapStandbyWithInProgressTailing
extends|extends
name|TestBootstrapStandbyWithQJM
block|{
annotation|@
name|Override
DECL|method|createConfig ()
specifier|public
name|Configuration
name|createConfig
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createConfig
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_HA_TAILEDITS_INPROGRESS_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"dfs.ha.tail-edits.qjm.rpc.max-txns"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
end_class

end_unit

