begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|flow
operator|.
name|Attribute
import|;
end_import

begin_comment
comment|/**  * A Column represents the way to store a fully qualified column in a specific  * table.  */
end_comment

begin_interface
DECL|interface|Column
specifier|public
interface|interface
name|Column
parameter_list|<
name|T
extends|extends
name|BaseTable
parameter_list|<
name|T
parameter_list|>
parameter_list|>
block|{
comment|/**    * Returns column family name(as bytes) associated with this column.    * @return a byte array encoding column family for this column qualifier.    */
DECL|method|getColumnFamilyBytes ()
name|byte
index|[]
name|getColumnFamilyBytes
parameter_list|()
function_decl|;
comment|/**    * Get byte representation for this column qualifier.    * @return a byte array representing column qualifier.    */
DECL|method|getColumnQualifierBytes ()
name|byte
index|[]
name|getColumnQualifierBytes
parameter_list|()
function_decl|;
comment|/**    * Returns value converter implementation associated with this column.    * @return a {@link ValueConverter} implementation.    */
DECL|method|getValueConverter ()
name|ValueConverter
name|getValueConverter
parameter_list|()
function_decl|;
comment|/**    * Return attributed combined with aggregations, if any.    * @return an array of Attributes    */
DECL|method|getCombinedAttrsWithAggr (Attribute... attributes)
name|Attribute
index|[]
name|getCombinedAttrsWithAggr
parameter_list|(
name|Attribute
modifier|...
name|attributes
parameter_list|)
function_decl|;
comment|/**    * Return true if the cell timestamp needs to be supplemented.    * @return true if the cell timestamp needs to be supplemented    */
DECL|method|supplementCellTimestamp ()
name|boolean
name|supplementCellTimestamp
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

