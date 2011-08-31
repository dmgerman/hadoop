begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.http.resource
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
operator|.
name|resource
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|DefaultValue
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
name|GET
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
name|Path
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
name|PathParam
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
name|Produces
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
name|QueryParam
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
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_comment
comment|/**  * A simple Jersey resource class TestHttpServer.  * The servlet simply puts the path and the op parameter in a map  * and return it in JSON format in the response.  */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|""
argument_list|)
DECL|class|JerseyResource
specifier|public
class|class
name|JerseyResource
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JerseyResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PATH
specifier|public
specifier|static
specifier|final
name|String
name|PATH
init|=
literal|"path"
decl_stmt|;
DECL|field|OP
specifier|public
specifier|static
specifier|final
name|String
name|OP
init|=
literal|"op"
decl_stmt|;
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"{"
operator|+
name|PATH
operator|+
literal|":.*}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|get ( @athParamPATH) @efaultValueR + PATH) final String path, @QueryParam(OP) @DefaultValue(R + OP) final String op )
specifier|public
name|Response
name|get
parameter_list|(
annotation|@
name|PathParam
argument_list|(
name|PATH
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
literal|"UNKNOWN_"
operator|+
name|PATH
argument_list|)
specifier|final
name|String
name|path
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|OP
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
literal|"UNKNOWN_"
operator|+
name|OP
argument_list|)
specifier|final
name|String
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"get: "
operator|+
name|PATH
operator|+
literal|"="
operator|+
name|path
operator|+
literal|", "
operator|+
name|OP
operator|+
literal|"="
operator|+
name|op
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|PATH
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|OP
argument_list|,
name|op
argument_list|)
expr_stmt|;
specifier|final
name|String
name|js
init|=
name|JSON
operator|.
name|toString
argument_list|(
name|m
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|js
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

