begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.checkpoint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|checkpoint
package|;
end_package

begin_comment
comment|/**  * A naming service that simply returns the name it has been initialized with.  */
end_comment

begin_class
DECL|class|SimpleNamingService
specifier|public
class|class
name|SimpleNamingService
implements|implements
name|CheckpointNamingService
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|SimpleNamingService (String name)
specifier|public
name|SimpleNamingService
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
block|}
comment|/**    * Generate a new checkpoint Name    * @return the checkpoint name    */
DECL|method|getNewName ()
specifier|public
name|String
name|getNewName
parameter_list|()
block|{
return|return
literal|"checkpoint_"
operator|+
name|name
return|;
block|}
block|}
end_class

end_unit

