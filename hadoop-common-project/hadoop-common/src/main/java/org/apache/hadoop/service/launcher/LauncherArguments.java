begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.service.launcher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|launcher
package|;
end_package

begin_comment
comment|/**  * Standard launcher arguments. These are all from  * the {@code GenericOptionsParser}, simply extracted to constants.  */
end_comment

begin_interface
DECL|interface|LauncherArguments
specifier|public
interface|interface
name|LauncherArguments
block|{
comment|/**    * Name of the configuration argument on the CLI.    * Value: {@value}    */
DECL|field|ARG_CONF
name|String
name|ARG_CONF
init|=
literal|"conf"
decl_stmt|;
DECL|field|ARG_CONF_SHORT
name|String
name|ARG_CONF_SHORT
init|=
literal|"conf"
decl_stmt|;
comment|/**    * prefixed version of {@link #ARG_CONF}.    * Value: {@value}    */
DECL|field|ARG_CONF_PREFIXED
name|String
name|ARG_CONF_PREFIXED
init|=
literal|"--"
operator|+
name|ARG_CONF
decl_stmt|;
comment|/**    * Name of a configuration class which is loaded before any    * attempt is made to load the class.    *<p>    * Value: {@value}    */
DECL|field|ARG_CONFCLASS
name|String
name|ARG_CONFCLASS
init|=
literal|"hadoopconf"
decl_stmt|;
DECL|field|ARG_CONFCLASS_SHORT
name|String
name|ARG_CONFCLASS_SHORT
init|=
literal|"hadoopconf"
decl_stmt|;
comment|/**    * Prefixed version of {@link #ARG_CONFCLASS}.    * Value: {@value}    */
DECL|field|ARG_CONFCLASS_PREFIXED
name|String
name|ARG_CONFCLASS_PREFIXED
init|=
literal|"--"
operator|+
name|ARG_CONFCLASS
decl_stmt|;
comment|/**    * Error string on a parse failure.    * Value: {@value}    */
DECL|field|E_PARSE_FAILED
name|String
name|E_PARSE_FAILED
init|=
literal|"Failed to parse: "
decl_stmt|;
block|}
end_interface

end_unit

