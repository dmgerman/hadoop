begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.types
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|types
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Service record header; access to the byte array kept private  * to avoid findbugs warnings of mutability  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ServiceRecordHeader
specifier|public
class|class
name|ServiceRecordHeader
block|{
comment|/**    * Header of a service record:  "jsonservicerec"    * By making this over 12 bytes long, we can auto-determine which entries    * in a listing are too short to contain a record without getting their data    */
DECL|field|RECORD_HEADER
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|RECORD_HEADER
init|=
block|{
literal|'j'
block|,
literal|'s'
block|,
literal|'o'
block|,
literal|'n'
block|,
literal|'s'
block|,
literal|'e'
block|,
literal|'r'
block|,
literal|'v'
block|,
literal|'i'
block|,
literal|'c'
block|,
literal|'e'
block|,
literal|'r'
block|,
literal|'e'
block|,
literal|'c'
block|}
decl_stmt|;
comment|/**    * Get the length of the record header    * @return the header length    */
DECL|method|getLength ()
specifier|public
specifier|static
name|int
name|getLength
parameter_list|()
block|{
return|return
name|RECORD_HEADER
operator|.
name|length
return|;
block|}
comment|/**    * Get a clone of the record header    * @return the new record header.    */
DECL|method|getData ()
specifier|public
specifier|static
name|byte
index|[]
name|getData
parameter_list|()
block|{
name|byte
index|[]
name|h
init|=
operator|new
name|byte
index|[
name|RECORD_HEADER
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|RECORD_HEADER
argument_list|,
literal|0
argument_list|,
name|h
argument_list|,
literal|0
argument_list|,
name|RECORD_HEADER
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

