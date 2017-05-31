begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|server
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
name|lib
operator|.
name|lang
operator|.
name|XException
import|;
end_import

begin_comment
comment|/**  * Exception thrown by the {@link Server} class.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ServerException
specifier|public
class|class
name|ServerException
extends|extends
name|XException
block|{
comment|/**    * Error codes use by the {@link Server} class.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|ERROR
specifier|public
enum|enum
name|ERROR
implements|implements
name|XException
operator|.
name|ERROR
block|{
DECL|enumConstant|S01
name|S01
argument_list|(
literal|"Dir [{0}] does not exist"
argument_list|)
block|,
DECL|enumConstant|S02
name|S02
argument_list|(
literal|"[{0}] is not a directory"
argument_list|)
block|,
DECL|enumConstant|S03
name|S03
argument_list|(
literal|"Could not load file from classpath [{0}], {1}"
argument_list|)
block|,
DECL|enumConstant|S04
name|S04
argument_list|(
literal|"Service [{0}] does not implement declared interface [{1}]"
argument_list|)
block|,
DECL|enumConstant|S05
name|S05
argument_list|(
literal|"[{0}] is not a file"
argument_list|)
block|,
DECL|enumConstant|S06
name|S06
argument_list|(
literal|"Could not load file [{0}], {1}"
argument_list|)
block|,
DECL|enumConstant|S07
name|S07
argument_list|(
literal|"Could not instanciate service class [{0}], {1}"
argument_list|)
block|,
DECL|enumConstant|S08
name|S08
argument_list|(
literal|"Could not load service classes, {0}"
argument_list|)
block|,
DECL|enumConstant|S09
name|S09
argument_list|(
literal|"Could not set service [{0}] programmatically -server shutting down-, {1}"
argument_list|)
block|,
DECL|enumConstant|S10
name|S10
argument_list|(
literal|"Service [{0}] requires service [{1}]"
argument_list|)
block|,
DECL|enumConstant|S11
name|S11
argument_list|(
literal|"Service [{0}] exception during status change to [{1}] -server shutting down-, {2}"
argument_list|)
block|,
DECL|enumConstant|S12
name|S12
argument_list|(
literal|"Could not start service [{0}], {1}"
argument_list|)
block|,
DECL|enumConstant|S13
name|S13
argument_list|(
literal|"Missing system property [{0}]"
argument_list|)
block|,
DECL|enumConstant|S14
name|S14
argument_list|(
literal|"Could not initialize server, {0}"
argument_list|)
block|;
DECL|field|msg
specifier|private
name|String
name|msg
decl_stmt|;
comment|/**      * Constructor for the error code enum.      *      * @param msg message template.      */
DECL|method|ERROR (String msg)
specifier|private
name|ERROR
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|this
operator|.
name|msg
operator|=
name|msg
expr_stmt|;
block|}
comment|/**      * Returns the message template for the error code.      *      * @return the message template for the error code.      */
annotation|@
name|Override
DECL|method|getTemplate ()
specifier|public
name|String
name|getTemplate
parameter_list|()
block|{
return|return
name|msg
return|;
block|}
block|}
comment|/**    * Constructor for sub-classes.    *    * @param error error code for the XException.    * @param params parameters to use when creating the error message    * with the error code template.    */
DECL|method|ServerException (XException.ERROR error, Object... params)
specifier|protected
name|ServerException
parameter_list|(
name|XException
operator|.
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
comment|/**    * Creates an server exception using the specified error code.    * The exception message is resolved using the error code template    * and the passed parameters.    *    * @param error error code for the XException.    * @param params parameters to use when creating the error message    * with the error code template.    */
DECL|method|ServerException (ERROR error, Object... params)
specifier|public
name|ServerException
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

