begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.testutil
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|testutil
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
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|NativeMapOutputCollectorDelegator
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

begin_class
DECL|class|EnforceNativeOutputCollectorDelegator
specifier|public
class|class
name|EnforceNativeOutputCollectorDelegator
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|NativeMapOutputCollectorDelegator
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
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
name|EnforceNativeOutputCollectorDelegator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nativetaskloaded
specifier|private
name|boolean
name|nativetaskloaded
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|init (Context context)
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
try|try
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|nativetaskloaded
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|nativetaskloaded
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"load nativetask lib failed, Native-Task Delegation is disabled"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|collect (K key, V value, int partition)
specifier|public
name|void
name|collect
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|this
operator|.
name|nativetaskloaded
condition|)
block|{
name|super
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|partition
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// nothing to do.
block|}
block|}
block|}
end_class

end_unit

