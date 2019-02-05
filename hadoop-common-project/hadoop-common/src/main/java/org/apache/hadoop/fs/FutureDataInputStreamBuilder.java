begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|concurrent
operator|.
name|CompletableFuture
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

begin_comment
comment|/**  * Builder for input streams and subclasses whose return value is  * actually a completable future: this allows for better asynchronous  * operation.  *  * To be more generic, {@link #opt(String, int)} and {@link #must(String, int)}  * variants provide implementation-agnostic way to customize the builder.  * Each FS-specific builder implementation can interpret the FS-specific  * options accordingly, for example:  *  * If the option is not related to the file system, the option will be ignored.  * If the option is must, but not supported by the file system, a  * {@link IllegalArgumentException} will be thrown.  *  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|FutureDataInputStreamBuilder
specifier|public
interface|interface
name|FutureDataInputStreamBuilder
extends|extends
name|FSBuilder
argument_list|<
name|CompletableFuture
argument_list|<
name|FSDataInputStream
argument_list|>
argument_list|,
name|FutureDataInputStreamBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|build ()
name|CompletableFuture
argument_list|<
name|FSDataInputStream
argument_list|>
name|build
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|UnsupportedOperationException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

