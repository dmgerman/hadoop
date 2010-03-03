begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
package|;
end_package

begin_comment
comment|/**  * Indicates that a method has been passed illegal or invalid argument. This  * exception is thrown instead of IllegalArgumentException to differentiate the  * exception thrown in Hadoop implementation from the one thrown in JDK.  */
end_comment

begin_class
DECL|class|HadoopIllegalArgumentException
specifier|public
class|class
name|HadoopIllegalArgumentException
extends|extends
name|IllegalArgumentException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/**    * Constructs exception with the specified detail message.     * @param message detailed message.    */
DECL|method|HadoopIllegalArgumentException (final String message)
specifier|public
name|HadoopIllegalArgumentException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

