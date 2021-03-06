begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.common.serialization
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|common
operator|.
name|serialization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableMap
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|server
operator|.
name|resourcemanager
operator|.
name|reservation
operator|.
name|RLESparseResourceAllocation
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
name|util
operator|.
name|resource
operator|.
name|DefaultResourceCalculator
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
name|util
operator|.
name|resource
operator|.
name|ResourceCalculator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|Gson
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|GsonBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonDeserializationContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonDeserializer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonElement
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonParseException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonSerializationContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonSerializer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
import|;
end_import

begin_comment
comment|/**  * Serialize/deserialize RLESparseResourceAllocation object to/from JSON.  */
end_comment

begin_class
DECL|class|RLESparseResourceAllocationSerDe
specifier|public
class|class
name|RLESparseResourceAllocationSerDe
implements|implements
name|JsonSerializer
argument_list|<
name|RLESparseResourceAllocation
argument_list|>
implements|,
name|JsonDeserializer
argument_list|<
name|RLESparseResourceAllocation
argument_list|>
block|{
DECL|field|KEY
specifier|private
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"resourceAllocation"
decl_stmt|;
DECL|field|gson
specifier|private
specifier|final
name|Gson
name|gson
init|=
operator|new
name|GsonBuilder
argument_list|()
operator|.
name|registerTypeAdapter
argument_list|(
name|Resource
operator|.
name|class
argument_list|,
operator|new
name|ResourceSerDe
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|Type
name|type
init|=
operator|new
name|TypeToken
argument_list|<
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|>
argument_list|()
block|{   }
operator|.
name|getType
argument_list|()
decl_stmt|;
DECL|field|resourceCalculator
specifier|private
specifier|final
name|ResourceCalculator
name|resourceCalculator
init|=
operator|new
name|DefaultResourceCalculator
argument_list|()
decl_stmt|;
DECL|method|serialize ( final RLESparseResourceAllocation resourceAllocation, final Type typeOfSrc, final JsonSerializationContext context)
annotation|@
name|Override
specifier|public
specifier|final
name|JsonElement
name|serialize
parameter_list|(
specifier|final
name|RLESparseResourceAllocation
name|resourceAllocation
parameter_list|,
specifier|final
name|Type
name|typeOfSrc
parameter_list|,
specifier|final
name|JsonSerializationContext
name|context
parameter_list|)
block|{
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|myMap
init|=
name|resourceAllocation
operator|.
name|getCumulative
argument_list|()
decl_stmt|;
name|JsonObject
name|jo
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
name|JsonElement
name|element
init|=
name|gson
operator|.
name|toJsonTree
argument_list|(
name|myMap
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|jo
operator|.
name|add
argument_list|(
name|KEY
argument_list|,
name|element
argument_list|)
expr_stmt|;
return|return
name|jo
return|;
block|}
DECL|method|deserialize ( final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
annotation|@
name|Override
specifier|public
specifier|final
name|RLESparseResourceAllocation
name|deserialize
parameter_list|(
specifier|final
name|JsonElement
name|json
parameter_list|,
specifier|final
name|Type
name|typeOfT
parameter_list|,
specifier|final
name|JsonDeserializationContext
name|context
parameter_list|)
throws|throws
name|JsonParseException
block|{
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|resAllocation
init|=
name|gson
operator|.
name|fromJson
argument_list|(
name|json
operator|.
name|getAsJsonObject
argument_list|()
operator|.
name|get
argument_list|(
name|KEY
argument_list|)
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|RLESparseResourceAllocation
name|rleSparseResourceAllocation
init|=
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|resAllocation
argument_list|,
name|resourceCalculator
argument_list|)
decl_stmt|;
return|return
name|rleSparseResourceAllocation
return|;
block|}
block|}
end_class

end_unit

