begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_comment
comment|/**  * We use this exception class in the unit test, and we do a deep comparison  * when we run the  *   */
end_comment

begin_class
DECL|class|DeepInequalityException
specifier|public
class|class
name|DeepInequalityException
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1352469876
decl_stmt|;
DECL|field|path
specifier|final
name|TreePath
name|path
decl_stmt|;
comment|/**    * @param message    *          an exception message    * @param path    *          the path that gets from the root to the inequality    *     *          This is the constructor that I intend to have used for this    *          exception.    */
DECL|method|DeepInequalityException (String message, TreePath path, Throwable chainee)
specifier|public
name|DeepInequalityException
parameter_list|(
name|String
name|message
parameter_list|,
name|TreePath
name|path
parameter_list|,
name|Throwable
name|chainee
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|chainee
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
comment|/**    * @param message    *          an exception message    * @param path    *          the path that gets from the root to the inequality    *     *          This is the constructor that I intend to have used for this    *          exception.    */
DECL|method|DeepInequalityException (String message, TreePath path)
specifier|public
name|DeepInequalityException
parameter_list|(
name|String
name|message
parameter_list|,
name|TreePath
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
block|}
end_class

end_unit

