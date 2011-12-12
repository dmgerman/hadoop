begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.wsrs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|wsrs
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
name|fs
operator|.
name|http
operator|.
name|client
operator|.
name|HttpFSFileSystem
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|ExceptionMapper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|ExceptionProvider
specifier|public
class|class
name|ExceptionProvider
implements|implements
name|ExceptionMapper
argument_list|<
name|Throwable
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ExceptionProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ENTER
specifier|private
specifier|static
specifier|final
name|String
name|ENTER
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|method|createResponse (Response.Status status, Throwable throwable)
specifier|protected
name|Response
name|createResponse
parameter_list|(
name|Response
operator|.
name|Status
name|status
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|json
operator|.
name|put
argument_list|(
name|HttpFSFileSystem
operator|.
name|ERROR_MESSAGE_JSON
argument_list|,
name|getOneLineMessage
argument_list|(
name|throwable
argument_list|)
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|HttpFSFileSystem
operator|.
name|ERROR_EXCEPTION_JSON
argument_list|,
name|throwable
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|HttpFSFileSystem
operator|.
name|ERROR_CLASSNAME_JSON
argument_list|,
name|throwable
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
name|HttpFSFileSystem
operator|.
name|ERROR_JSON
argument_list|,
name|json
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|status
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|status
argument_list|(
name|status
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
name|response
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getOneLineMessage (Throwable throwable)
specifier|protected
name|String
name|getOneLineMessage
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|String
name|message
init|=
name|throwable
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|int
name|i
init|=
name|message
operator|.
name|indexOf
argument_list|(
name|ENTER
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
operator|-
literal|1
condition|)
block|{
name|message
operator|=
name|message
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|message
return|;
block|}
DECL|method|log (Response.Status status, Throwable throwable)
specifier|protected
name|void
name|log
parameter_list|(
name|Response
operator|.
name|Status
name|status
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}"
argument_list|,
name|throwable
operator|.
name|getMessage
argument_list|()
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toResponse (Throwable throwable)
specifier|public
name|Response
name|toResponse
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
return|return
name|createResponse
argument_list|(
name|Response
operator|.
name|Status
operator|.
name|BAD_REQUEST
argument_list|,
name|throwable
argument_list|)
return|;
block|}
block|}
end_class

end_unit

