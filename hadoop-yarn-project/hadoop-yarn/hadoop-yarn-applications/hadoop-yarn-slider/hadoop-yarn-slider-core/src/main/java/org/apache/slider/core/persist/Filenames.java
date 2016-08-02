begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.persist
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
package|;
end_package

begin_interface
DECL|interface|Filenames
specifier|public
interface|interface
name|Filenames
block|{
DECL|field|RESOURCES
name|String
name|RESOURCES
init|=
literal|"resources.json"
decl_stmt|;
DECL|field|APPCONF
name|String
name|APPCONF
init|=
literal|"app_config.json"
decl_stmt|;
DECL|field|INTERNAL
name|String
name|INTERNAL
init|=
literal|"internal.json"
decl_stmt|;
DECL|field|WRITELOCK
name|String
name|WRITELOCK
init|=
literal|"writelock"
decl_stmt|;
DECL|field|READLOCK
name|String
name|READLOCK
init|=
literal|"readlock"
decl_stmt|;
block|}
end_interface

end_unit

