begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tracing
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tracing
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|List
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
name|tracing
operator|.
name|SpanReceiverInfo
operator|.
name|ConfigurationPair
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|HTraceConfiguration
import|;
end_import

begin_comment
comment|/**  * This class provides utility functions for tracing.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|TraceUtils
specifier|public
class|class
name|TraceUtils
block|{
DECL|field|EMPTY
specifier|private
specifier|static
name|List
argument_list|<
name|ConfigurationPair
argument_list|>
name|EMPTY
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
DECL|method|wrapHadoopConf (final String prefix, final Configuration conf)
specifier|public
specifier|static
name|HTraceConfiguration
name|wrapHadoopConf
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|wrapHadoopConf
argument_list|(
name|prefix
argument_list|,
name|conf
argument_list|,
name|EMPTY
argument_list|)
return|;
block|}
DECL|method|wrapHadoopConf (final String prefix, final Configuration conf, List<ConfigurationPair> extraConfig)
specifier|public
specifier|static
name|HTraceConfiguration
name|wrapHadoopConf
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|ConfigurationPair
argument_list|>
name|extraConfig
parameter_list|)
block|{
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extraMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ConfigurationPair
name|pair
range|:
name|extraConfig
control|)
block|{
name|extraMap
operator|.
name|put
argument_list|(
name|pair
operator|.
name|getKey
argument_list|()
argument_list|,
name|pair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|HTraceConfiguration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|extraMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|extraMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
name|conf
operator|.
name|get
argument_list|(
name|prefix
operator|+
name|key
argument_list|,
literal|""
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|extraMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|extraMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
name|conf
operator|.
name|get
argument_list|(
name|prefix
operator|+
name|key
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

