begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.netty
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|netty
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelFuture
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelFutureListener
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_comment
comment|/**  * A {@link ChannelFutureListener} that closes {@link Closeable} resources.  */
end_comment

begin_class
DECL|class|CloseableCleanupListener
specifier|final
class|class
name|CloseableCleanupListener
implements|implements
name|ChannelFutureListener
block|{
DECL|field|closeables
specifier|private
specifier|final
name|Closeable
index|[]
name|closeables
decl_stmt|;
comment|/**    * Creates a new CloseableCleanupListener.    *    * @param closeables any number of closeable resources    */
DECL|method|CloseableCleanupListener (Closeable... closeables)
name|CloseableCleanupListener
parameter_list|(
name|Closeable
modifier|...
name|closeables
parameter_list|)
block|{
name|this
operator|.
name|closeables
operator|=
name|closeables
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|operationComplete (ChannelFuture future)
specifier|public
name|void
name|operationComplete
parameter_list|(
name|ChannelFuture
name|future
parameter_list|)
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|closeables
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

