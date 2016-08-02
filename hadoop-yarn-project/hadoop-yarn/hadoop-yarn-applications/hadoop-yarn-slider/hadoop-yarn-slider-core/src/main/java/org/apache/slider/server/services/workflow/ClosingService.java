begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.services.workflow
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
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
name|service
operator|.
name|AbstractService
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Service that closes the closeable supplied during shutdown, if not null.  *   * As the Service interface itself extends Closeable, this service  * can be used to shut down other services if desired.  */
end_comment

begin_class
DECL|class|ClosingService
specifier|public
class|class
name|ClosingService
parameter_list|<
name|C
extends|extends
name|Closeable
parameter_list|>
extends|extends
name|AbstractService
block|{
DECL|field|closeable
specifier|private
name|C
name|closeable
decl_stmt|;
DECL|method|ClosingService (String name)
specifier|public
name|ClosingService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct an instance of the service    * @param name service name    * @param closeable closeable to close (may be null)    */
DECL|method|ClosingService (String name, C closeable)
specifier|public
name|ClosingService
parameter_list|(
name|String
name|name
parameter_list|,
name|C
name|closeable
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|closeable
operator|=
name|closeable
expr_stmt|;
block|}
comment|/**    * Construct an instance of the service, using the default name    * @param closeable closeable to close (may be null)    */
DECL|method|ClosingService (C closeable)
specifier|public
name|ClosingService
parameter_list|(
name|C
name|closeable
parameter_list|)
block|{
name|this
argument_list|(
literal|"ClosingService"
argument_list|,
name|closeable
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the closeable    * @return the closeable    */
DECL|method|getCloseable ()
specifier|public
specifier|synchronized
name|C
name|getCloseable
parameter_list|()
block|{
return|return
name|closeable
return|;
block|}
comment|/**    * Set or update the closeable.    * @param closeable    */
DECL|method|setCloseable (C closeable)
specifier|public
specifier|synchronized
name|void
name|setCloseable
parameter_list|(
name|C
name|closeable
parameter_list|)
block|{
name|this
operator|.
name|closeable
operator|=
name|closeable
expr_stmt|;
block|}
comment|/**    * Stop routine will close the closeable -if not null - and set the    * reference to null afterwards    * This operation does raise any exception on the close, though it does    * record it    */
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
block|{
name|C
name|target
init|=
name|getCloseable
argument_list|()
decl_stmt|;
if|if
condition|(
name|target
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|target
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|noteFailure
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
block|}
name|setCloseable
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

