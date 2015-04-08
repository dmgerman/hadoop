begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
import|;
end_import

begin_comment
comment|/**  * Class to provide information, such as ECSchema, for a file/block.  */
end_comment

begin_class
DECL|class|ECInfo
specifier|public
class|class
name|ECInfo
block|{
DECL|field|src
specifier|private
specifier|final
name|String
name|src
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|ECSchema
name|schema
decl_stmt|;
DECL|method|ECInfo (String src, ECSchema schema)
specifier|public
name|ECInfo
parameter_list|(
name|String
name|src
parameter_list|,
name|ECSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|src
operator|=
name|src
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
DECL|method|getSrc ()
specifier|public
name|String
name|getSrc
parameter_list|()
block|{
return|return
name|src
return|;
block|}
DECL|method|getSchema ()
specifier|public
name|ECSchema
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
block|}
end_class

end_unit

