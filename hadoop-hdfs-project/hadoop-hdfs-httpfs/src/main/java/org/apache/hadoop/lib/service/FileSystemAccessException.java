begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|service
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
name|lib
operator|.
name|lang
operator|.
name|XException
import|;
end_import

begin_class
DECL|class|FileSystemAccessException
specifier|public
class|class
name|FileSystemAccessException
extends|extends
name|XException
block|{
DECL|enum|ERROR
specifier|public
enum|enum
name|ERROR
implements|implements
name|XException
operator|.
name|ERROR
block|{
DECL|enumConstant|H01
name|H01
argument_list|(
literal|"Service property [{0}] not defined"
argument_list|)
block|,
DECL|enumConstant|H02
name|H02
argument_list|(
literal|"Kerberos initialization failed, {0}"
argument_list|)
block|,
DECL|enumConstant|H03
name|H03
argument_list|(
literal|"FileSystemExecutor error, {0}"
argument_list|)
block|,
DECL|enumConstant|H04
name|H04
argument_list|(
literal|"Invalid configuration, it has not be created by the FileSystemAccessService"
argument_list|)
block|,
DECL|enumConstant|H05
name|H05
argument_list|(
literal|"[{0}] validation failed, {1}"
argument_list|)
block|,
DECL|enumConstant|H06
name|H06
argument_list|(
literal|"Property [{0}] not defined in configuration object"
argument_list|)
block|,
DECL|enumConstant|H07
name|H07
argument_list|(
literal|"[{0}] not healthy, {1}"
argument_list|)
block|,
DECL|enumConstant|H08
name|H08
argument_list|(
literal|"{0}"
argument_list|)
block|,
DECL|enumConstant|H09
name|H09
argument_list|(
literal|"Invalid FileSystemAccess security mode [{0}]"
argument_list|)
block|,
DECL|enumConstant|H10
name|H10
argument_list|(
literal|"Hadoop config directory not found [{0}]"
argument_list|)
block|,
DECL|enumConstant|H11
name|H11
argument_list|(
literal|"Could not load Hadoop config files, {0}"
argument_list|)
block|;
DECL|field|template
specifier|private
name|String
name|template
decl_stmt|;
DECL|method|ERROR (String template)
name|ERROR
parameter_list|(
name|String
name|template
parameter_list|)
block|{
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTemplate ()
specifier|public
name|String
name|getTemplate
parameter_list|()
block|{
return|return
name|template
return|;
block|}
block|}
DECL|method|FileSystemAccessException (ERROR error, Object... params)
specifier|public
name|FileSystemAccessException
parameter_list|(
name|ERROR
name|error
parameter_list|,
name|Object
modifier|...
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|error
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

