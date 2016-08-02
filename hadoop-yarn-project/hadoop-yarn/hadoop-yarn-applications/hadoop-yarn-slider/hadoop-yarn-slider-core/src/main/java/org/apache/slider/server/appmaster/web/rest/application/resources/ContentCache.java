begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest.application.resources
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|application
operator|.
name|resources
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Cache of content  */
end_comment

begin_class
DECL|class|ContentCache
specifier|public
class|class
name|ContentCache
extends|extends
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|CachedContent
argument_list|>
block|{
DECL|method|ContentCache (int initialCapacity)
specifier|public
name|ContentCache
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|)
expr_stmt|;
block|}
DECL|method|ContentCache ()
specifier|public
name|ContentCache
parameter_list|()
block|{   }
DECL|method|lookup (String key)
specifier|public
name|Object
name|lookup
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
name|CachedContent
name|content
init|=
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|content
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"no content for path "
operator|+
name|key
argument_list|)
throw|;
block|}
return|return
name|content
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Lookup a cached item. If an exception is raised on the refresh...    *<ol>    *<li>IOExceptions are thrown directly</li>    *<li>Other exceptions are wrapped with an IOExceptions</li>    *</ol>    * @param key    * @return    * @throws IOException    */
DECL|method|lookupWithIOE (String key)
specifier|public
name|Object
name|lookupWithIOE
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|lookup
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Looking up "
operator|+
name|key
operator|+
literal|": "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

