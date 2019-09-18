begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.freon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|freon
package|;
end_package

begin_comment
comment|/**  * Class to generate the path based on a counter.  */
end_comment

begin_class
DECL|class|PathSchema
specifier|public
class|class
name|PathSchema
block|{
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|method|PathSchema (String prefix)
specifier|public
name|PathSchema
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
comment|/**    * Return with a relative path based on the current counter.    *<p>    * A more advanced implementation can generate deep directory hierarchy.    */
DECL|method|getPath (long counter)
specifier|public
name|String
name|getPath
parameter_list|(
name|long
name|counter
parameter_list|)
block|{
return|return
name|prefix
operator|+
literal|"/"
operator|+
name|counter
return|;
block|}
block|}
end_class

end_unit

