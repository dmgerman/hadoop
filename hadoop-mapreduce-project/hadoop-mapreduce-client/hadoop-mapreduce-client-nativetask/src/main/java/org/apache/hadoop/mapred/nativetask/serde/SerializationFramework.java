begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.serde
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|serde
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

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|SerializationFramework
specifier|public
enum|enum
name|SerializationFramework
block|{
DECL|enumConstant|WRITABLE_SERIALIZATION
DECL|enumConstant|NATIVE_SERIALIZATION
name|WRITABLE_SERIALIZATION
argument_list|(
literal|0
argument_list|)
block|,
name|NATIVE_SERIALIZATION
argument_list|(
literal|1
argument_list|)
block|;
DECL|field|type
specifier|private
name|int
name|type
decl_stmt|;
DECL|method|SerializationFramework (int type)
name|SerializationFramework
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|getType ()
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
end_enum

begin_empty_stmt
empty_stmt|;
end_empty_stmt

end_unit

