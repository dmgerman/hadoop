begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.applications.distributedshell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|applications
operator|.
name|distributedshell
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
name|api
operator|.
name|resource
operator|.
name|PlacementConstraint
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
name|constraint
operator|.
name|PlacementConstraintParseException
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
name|constraint
operator|.
name|PlacementConstraintParser
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
name|constraint
operator|.
name|PlacementConstraintParser
operator|.
name|SourceTags
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Class encapsulating a SourceTag, number of container and a Placement  * Constraint.  */
end_comment

begin_class
DECL|class|PlacementSpec
specifier|public
class|class
name|PlacementSpec
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PlacementSpec
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sourceTag
specifier|public
specifier|final
name|String
name|sourceTag
decl_stmt|;
DECL|field|numContainers
specifier|public
specifier|final
name|int
name|numContainers
decl_stmt|;
DECL|field|constraint
specifier|public
specifier|final
name|PlacementConstraint
name|constraint
decl_stmt|;
DECL|method|PlacementSpec (String sourceTag, int numContainers, PlacementConstraint constraint)
specifier|public
name|PlacementSpec
parameter_list|(
name|String
name|sourceTag
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|PlacementConstraint
name|constraint
parameter_list|)
block|{
name|this
operator|.
name|sourceTag
operator|=
name|sourceTag
expr_stmt|;
name|this
operator|.
name|numContainers
operator|=
name|numContainers
expr_stmt|;
name|this
operator|.
name|constraint
operator|=
name|constraint
expr_stmt|;
block|}
comment|// Placement specification should be of the form:
comment|// PlacementSpec => ""|KeyVal;PlacementSpec
comment|// KeyVal => SourceTag=Constraint
comment|// SourceTag => String
comment|// Constraint => NumContainers|
comment|//               NumContainers,"in",Scope,TargetTag|
comment|//               NumContainers,"notin",Scope,TargetTag|
comment|//               NumContainers,"cardinality",Scope,TargetTag,MinCard,MaxCard
comment|// NumContainers => int (number of containers)
comment|// Scope => "NODE"|"RACK"
comment|// TargetTag => String (Target Tag)
comment|// MinCard => int (min cardinality - needed if ConstraintType == cardinality)
comment|// MaxCard => int (max cardinality - needed if ConstraintType == cardinality)
comment|/**    * Parser to convert a string representation of a placement spec to mapping    * from source tag to Placement Constraint.    *    * @param specs Placement spec.    * @return Mapping from source tag to placement constraint.    */
DECL|method|parse (String specs)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|PlacementSpec
argument_list|>
name|parse
parameter_list|(
name|String
name|specs
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Parsing Placement Specs: [{}]"
argument_list|,
name|specs
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|PlacementSpec
argument_list|>
name|pSpecs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|SourceTags
argument_list|,
name|PlacementConstraint
argument_list|>
name|parsed
decl_stmt|;
try|try
block|{
name|parsed
operator|=
name|PlacementConstraintParser
operator|.
name|parsePlacementSpec
argument_list|(
name|specs
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|SourceTags
argument_list|,
name|PlacementConstraint
argument_list|>
name|entry
range|:
name|parsed
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Parsed source tag: {}, number of allocations: {}"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getTag
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Parsed constraint: {}"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getConstraintExpr
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|pSpecs
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getTag
argument_list|()
argument_list|,
operator|new
name|PlacementSpec
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getTag
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getNumOfAllocations
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|pSpecs
return|;
block|}
catch|catch
parameter_list|(
name|PlacementConstraintParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid placement spec: "
operator|+
name|specs
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

