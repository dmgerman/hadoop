begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli.param
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|ParseException
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|CliConstants
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|ClientContext
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|conf
operator|.
name|SubmarineLogs
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
comment|/**  * Base class of all parameters.  */
end_comment

begin_class
DECL|class|BaseParameters
specifier|public
specifier|abstract
class|class
name|BaseParameters
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|updateParameters (ParametersHolder parametersHolder, ClientContext clientContext)
specifier|public
name|void
name|updateParameters
parameter_list|(
name|ParametersHolder
name|parametersHolder
parameter_list|,
name|ClientContext
name|clientContext
parameter_list|)
throws|throws
name|ParseException
throws|,
name|IOException
throws|,
name|YarnException
block|{
name|String
name|name
init|=
name|parametersHolder
operator|.
name|getOptionValue
argument_list|(
name|CliConstants
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"--name is absent"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parametersHolder
operator|.
name|hasOption
argument_list|(
name|CliConstants
operator|.
name|VERBOSE
argument_list|)
condition|)
block|{
name|SubmarineLogs
operator|.
name|verboseOn
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|BaseParameters
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

