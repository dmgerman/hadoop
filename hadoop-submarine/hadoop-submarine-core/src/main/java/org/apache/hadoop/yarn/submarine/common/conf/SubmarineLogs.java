begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.common.conf
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
name|common
operator|.
name|conf
package|;
end_package

begin_class
DECL|class|SubmarineLogs
specifier|public
class|class
name|SubmarineLogs
block|{
DECL|field|verbose
specifier|private
specifier|static
specifier|volatile
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
DECL|method|isVerbose ()
specifier|public
specifier|static
name|boolean
name|isVerbose
parameter_list|()
block|{
return|return
name|SubmarineLogs
operator|.
name|verbose
return|;
block|}
DECL|method|verboseOn ()
specifier|public
specifier|static
name|void
name|verboseOn
parameter_list|()
block|{
name|SubmarineLogs
operator|.
name|verbose
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|verboseOff ()
specifier|public
specifier|static
name|void
name|verboseOff
parameter_list|()
block|{
name|SubmarineLogs
operator|.
name|verbose
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

