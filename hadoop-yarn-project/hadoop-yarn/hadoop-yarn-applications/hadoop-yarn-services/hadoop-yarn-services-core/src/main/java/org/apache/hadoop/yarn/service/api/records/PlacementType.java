begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
package|;
end_package

begin_import
import|import
name|io
operator|.
name|swagger
operator|.
name|annotations
operator|.
name|ApiModel
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
comment|/**  * The type of placement - affinity/anti-affinity/affinity-with-cardinality with  * containers of another component or containers of the same component (self).  **/
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|ApiModel
argument_list|(
name|description
operator|=
literal|"The type of placement - affinity/anti-affinity/"
operator|+
literal|"affinity-with-cardinality with containers of another component or "
operator|+
literal|"containers of the same component (self)."
argument_list|)
DECL|enum|PlacementType
specifier|public
enum|enum
name|PlacementType
block|{
DECL|enumConstant|AFFINITY
DECL|enumConstant|ANTI_AFFINITY
DECL|enumConstant|AFFINITY_WITH_CARDINALITY
name|AFFINITY
block|,
name|ANTI_AFFINITY
block|,
name|AFFINITY_WITH_CARDINALITY
block|; }
end_enum

end_unit

