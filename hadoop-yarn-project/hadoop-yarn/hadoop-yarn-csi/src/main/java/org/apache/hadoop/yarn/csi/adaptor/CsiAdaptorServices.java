begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.csi.adaptor
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|csi
operator|.
name|adaptor
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
name|yarn
operator|.
name|api
operator|.
name|CsiAdaptorPlugin
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
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|ApplicationInitializationContext
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
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|ApplicationTerminationContext
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
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|AuxiliaryService
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
name|yarn
operator|.
name|util
operator|.
name|csi
operator|.
name|CsiConfigUtils
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|List
import|;
end_import

begin_comment
comment|/**  * NM manages csi-adaptors as a single NM AUX service, this service  * manages a set of rpc services and each of them serves one particular  * csi-driver. It loads all available drivers from configuration, and  * find a csi-driver-adaptor implementation class for each of them. At last  * it brings up all of them as a composite service.  */
end_comment

begin_class
DECL|class|CsiAdaptorServices
specifier|public
class|class
name|CsiAdaptorServices
extends|extends
name|AuxiliaryService
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
name|CsiAdaptorServices
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serviceList
specifier|private
name|List
argument_list|<
name|CsiAdaptorProtocolService
argument_list|>
name|serviceList
decl_stmt|;
DECL|method|CsiAdaptorServices ()
specifier|protected
name|CsiAdaptorServices
parameter_list|()
block|{
name|super
argument_list|(
name|CsiAdaptorServices
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|serviceList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
comment|// load configuration and init adaptors
name|String
index|[]
name|names
init|=
name|CsiConfigUtils
operator|.
name|getCsiDriverNames
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|!=
literal|null
operator|&&
name|names
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|driverName
range|:
name|names
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding csi-driver-adaptor for csi-driver {}"
argument_list|,
name|driverName
argument_list|)
expr_stmt|;
name|CsiAdaptorPlugin
name|serviceImpl
init|=
name|CsiAdaptorFactory
operator|.
name|getAdaptor
argument_list|(
name|driverName
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|serviceImpl
operator|.
name|init
argument_list|(
name|driverName
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|CsiAdaptorProtocolService
name|service
init|=
operator|new
name|CsiAdaptorProtocolService
argument_list|(
name|serviceImpl
argument_list|)
decl_stmt|;
name|serviceList
operator|.
name|add
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|service
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|serviceList
operator|!=
literal|null
operator|&&
name|serviceList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|CsiAdaptorProtocolService
name|service
range|:
name|serviceList
control|)
block|{
try|try
block|{
name|service
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to stop service "
operator|+
name|service
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|serviceList
operator|!=
literal|null
operator|&&
name|serviceList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|CsiAdaptorProtocolService
name|service
range|:
name|serviceList
control|)
block|{
name|service
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|initializeApplication ( ApplicationInitializationContext initAppContext)
specifier|public
name|void
name|initializeApplication
parameter_list|(
name|ApplicationInitializationContext
name|initAppContext
parameter_list|)
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|stopApplication ( ApplicationTerminationContext stopAppContext)
specifier|public
name|void
name|stopApplication
parameter_list|(
name|ApplicationTerminationContext
name|stopAppContext
parameter_list|)
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|getMetaData ()
specifier|public
name|ByteBuffer
name|getMetaData
parameter_list|()
block|{
return|return
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

