begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
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
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|ReflectionUtils
import|;
end_import

begin_class
DECL|class|HistoryServerStateStoreServiceFactory
specifier|public
class|class
name|HistoryServerStateStoreServiceFactory
block|{
comment|/**    * Constructs an instance of the configured storage class    *     * @param conf the configuration    * @return the state storage instance    */
DECL|method|getStore (Configuration conf)
specifier|public
specifier|static
name|HistoryServerStateStoreService
name|getStore
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|HistoryServerStateStoreService
argument_list|>
name|storeClass
init|=
name|HistoryServerNullStateStoreService
operator|.
name|class
decl_stmt|;
name|boolean
name|recoveryEnabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_RECOVERY_ENABLE
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HS_RECOVERY_ENABLE
argument_list|)
decl_stmt|;
if|if
condition|(
name|recoveryEnabled
condition|)
block|{
name|storeClass
operator|=
name|conf
operator|.
name|getClass
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_STATE_STORE
argument_list|,
literal|null
argument_list|,
name|HistoryServerStateStoreService
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|storeClass
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to locate storage class, check "
operator|+
name|JHAdminConfig
operator|.
name|MR_HS_STATE_STORE
argument_list|)
throw|;
block|}
block|}
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|storeClass
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

