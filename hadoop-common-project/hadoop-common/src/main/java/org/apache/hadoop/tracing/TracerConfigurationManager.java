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
name|io
operator|.
name|IOException
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
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|core
operator|.
name|SpanReceiver
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
name|core
operator|.
name|TracerPool
import|;
end_import

begin_comment
comment|/**  * This class provides functions for managing the tracer configuration at  * runtime via an RPC protocol.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|TracerConfigurationManager
specifier|public
class|class
name|TracerConfigurationManager
implements|implements
name|TraceAdminProtocol
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TracerConfigurationManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|confPrefix
specifier|private
specifier|final
name|String
name|confPrefix
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|method|TracerConfigurationManager (String confPrefix, Configuration conf)
specifier|public
name|TracerConfigurationManager
parameter_list|(
name|String
name|confPrefix
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|confPrefix
operator|=
name|confPrefix
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|listSpanReceivers ()
specifier|public
specifier|synchronized
name|SpanReceiverInfo
index|[]
name|listSpanReceivers
parameter_list|()
throws|throws
name|IOException
block|{
name|TracerPool
name|pool
init|=
name|TracerPool
operator|.
name|getGlobalTracerPool
argument_list|()
decl_stmt|;
name|SpanReceiver
index|[]
name|receivers
init|=
name|pool
operator|.
name|getReceivers
argument_list|()
decl_stmt|;
name|SpanReceiverInfo
index|[]
name|info
init|=
operator|new
name|SpanReceiverInfo
index|[
name|receivers
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|receivers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SpanReceiver
name|receiver
init|=
name|receivers
index|[
name|i
index|]
decl_stmt|;
name|info
index|[
name|i
index|]
operator|=
operator|new
name|SpanReceiverInfo
argument_list|(
name|receiver
operator|.
name|getId
argument_list|()
argument_list|,
name|receiver
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
DECL|method|addSpanReceiver (SpanReceiverInfo info)
specifier|public
specifier|synchronized
name|long
name|addSpanReceiver
parameter_list|(
name|SpanReceiverInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|configStringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
for|for
control|(
name|ConfigurationPair
name|pair
range|:
name|info
operator|.
name|configPairs
control|)
block|{
name|configStringBuilder
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
name|pair
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" = "
argument_list|)
operator|.
name|append
argument_list|(
name|pair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|", "
expr_stmt|;
block|}
name|SpanReceiver
name|rcvr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rcvr
operator|=
operator|new
name|SpanReceiver
operator|.
name|Builder
argument_list|(
name|TraceUtils
operator|.
name|wrapHadoopConf
argument_list|(
name|confPrefix
argument_list|,
name|conf
argument_list|,
name|info
operator|.
name|configPairs
argument_list|)
argument_list|)
operator|.
name|className
argument_list|(
name|info
operator|.
name|getClassName
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to add SpanReceiver "
operator|+
name|info
operator|.
name|getClassName
argument_list|()
operator|+
literal|" with configuration "
operator|+
name|configStringBuilder
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|TracerPool
operator|.
name|getGlobalTracerPool
argument_list|()
operator|.
name|addReceiver
argument_list|(
name|rcvr
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully added SpanReceiver "
operator|+
name|info
operator|.
name|getClassName
argument_list|()
operator|+
literal|" with configuration "
operator|+
name|configStringBuilder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rcvr
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|removeSpanReceiver (long spanReceiverId)
specifier|public
specifier|synchronized
name|void
name|removeSpanReceiver
parameter_list|(
name|long
name|spanReceiverId
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanReceiver
index|[]
name|receivers
init|=
name|TracerPool
operator|.
name|getGlobalTracerPool
argument_list|()
operator|.
name|getReceivers
argument_list|()
decl_stmt|;
for|for
control|(
name|SpanReceiver
name|receiver
range|:
name|receivers
control|)
block|{
if|if
condition|(
name|receiver
operator|.
name|getId
argument_list|()
operator|==
name|spanReceiverId
condition|)
block|{
name|TracerPool
operator|.
name|getGlobalTracerPool
argument_list|()
operator|.
name|removeAndCloseReceiver
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully removed SpanReceiver "
operator|+
name|spanReceiverId
operator|+
literal|" with class "
operator|+
name|receiver
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"There is no span receiver with id "
operator|+
name|spanReceiverId
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

