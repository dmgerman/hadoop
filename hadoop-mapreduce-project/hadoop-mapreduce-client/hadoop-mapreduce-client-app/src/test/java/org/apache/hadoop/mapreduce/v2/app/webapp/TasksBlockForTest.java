begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp
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
name|app
operator|.
name|webapp
package|;
end_package

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
name|Map
import|;
end_import

begin_comment
comment|/**  *    Class TasksBlockForTest overrides some methods for test  */
end_comment

begin_class
DECL|class|TasksBlockForTest
specifier|public
class|class
name|TasksBlockForTest
extends|extends
name|TasksBlock
block|{
DECL|field|params
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
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
DECL|method|TasksBlockForTest (App app)
specifier|public
name|TasksBlockForTest
parameter_list|(
name|App
name|app
parameter_list|)
block|{
name|super
argument_list|(
name|app
argument_list|)
expr_stmt|;
block|}
DECL|method|addParameter (String name, String value)
specifier|public
name|void
name|addParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|params
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|$ (String key, String defaultValue)
specifier|public
name|String
name|$
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|value
operator|==
literal|null
condition|?
name|defaultValue
else|:
name|value
return|;
block|}
DECL|method|url (String... parts)
specifier|public
name|String
name|url
parameter_list|(
name|String
modifier|...
name|parts
parameter_list|)
block|{
name|String
name|result
init|=
literal|"url://"
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|parts
control|)
block|{
name|result
operator|+=
name|string
operator|+
literal|":"
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

