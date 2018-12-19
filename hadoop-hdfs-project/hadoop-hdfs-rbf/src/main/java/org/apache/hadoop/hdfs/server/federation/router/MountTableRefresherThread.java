begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
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
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|MountTableManager
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|RefreshMountTableEntriesRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|RefreshMountTableEntriesResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Base class for updating mount table cache on all the router.  */
end_comment

begin_class
DECL|class|MountTableRefresherThread
specifier|public
class|class
name|MountTableRefresherThread
extends|extends
name|Thread
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MountTableRefresherThread
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|success
specifier|private
name|boolean
name|success
decl_stmt|;
comment|/** Admin server on which refreshed to be invoked. */
DECL|field|adminAddress
specifier|private
name|String
name|adminAddress
decl_stmt|;
DECL|field|countDownLatch
specifier|private
name|CountDownLatch
name|countDownLatch
decl_stmt|;
DECL|field|manager
specifier|private
name|MountTableManager
name|manager
decl_stmt|;
DECL|method|MountTableRefresherThread (MountTableManager manager, String adminAddress)
specifier|public
name|MountTableRefresherThread
parameter_list|(
name|MountTableManager
name|manager
parameter_list|,
name|String
name|adminAddress
parameter_list|)
block|{
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|adminAddress
operator|=
name|adminAddress
expr_stmt|;
name|setName
argument_list|(
literal|"MountTableRefresh_"
operator|+
name|adminAddress
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Refresh mount table cache of local and remote routers. Local and remote    * routers will be refreshed differently. Lets understand what are the    * local and remote routers and refresh will be done differently on these    * routers. Suppose there are three routers R1, R2 and R3. User want to add    * new mount table entry. He will connect to only one router, not all the    * routers. Suppose He connects to R1 and calls add mount table entry through    * API or CLI. Now in this context R1 is local router, R2 and R3 are remote    * routers. Because add mount table entry is invoked on R1, R1 will update the    * cache locally it need not to make RPC call. But R1 will make RPC calls to    * update cache on R2 and R3.    */
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|RefreshMountTableEntriesResponse
name|refreshMountTableEntries
init|=
name|manager
operator|.
name|refreshMountTableEntries
argument_list|(
name|RefreshMountTableEntriesRequest
operator|.
name|newInstance
argument_list|()
argument_list|)
decl_stmt|;
name|success
operator|=
name|refreshMountTableEntries
operator|.
name|getResult
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to refresh mount table entries cache at router {}"
argument_list|,
name|adminAddress
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|countDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * @return true if cache was refreshed successfully.    */
DECL|method|isSuccess ()
specifier|public
name|boolean
name|isSuccess
parameter_list|()
block|{
return|return
name|success
return|;
block|}
DECL|method|setCountDownLatch (CountDownLatch countDownLatch)
specifier|public
name|void
name|setCountDownLatch
parameter_list|(
name|CountDownLatch
name|countDownLatch
parameter_list|)
block|{
name|this
operator|.
name|countDownLatch
operator|=
name|countDownLatch
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MountTableRefreshThread [success="
operator|+
name|success
operator|+
literal|", adminAddress="
operator|+
name|adminAddress
operator|+
literal|"]"
return|;
block|}
DECL|method|getAdminAddress ()
specifier|public
name|String
name|getAdminAddress
parameter_list|()
block|{
return|return
name|adminAddress
return|;
block|}
block|}
end_class

end_unit

