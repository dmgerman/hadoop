begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.aggregate
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|aggregate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableComparable
import|;
end_import

begin_comment
comment|/**  * This abstract class implements some common functionalities of the  * the generic mapper, reducer and combiner classes of Aggregate.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ValueAggregatorJobBase
specifier|public
class|class
name|ValueAggregatorJobBase
parameter_list|<
name|K1
extends|extends
name|WritableComparable
parameter_list|<
name|?
parameter_list|>
parameter_list|,
name|V1
extends|extends
name|Writable
parameter_list|>
block|{
DECL|field|DESCRIPTOR
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTOR
init|=
literal|"mapreduce.aggregate.descriptor"
decl_stmt|;
DECL|field|DESCRIPTOR_NUM
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTOR_NUM
init|=
literal|"mapreduce.aggregate.descriptor.num"
decl_stmt|;
DECL|field|USER_JAR
specifier|public
specifier|static
specifier|final
name|String
name|USER_JAR
init|=
literal|"mapreduce.aggregate.user.jar.file"
decl_stmt|;
DECL|field|aggregatorDescriptorList
specifier|protected
specifier|static
name|ArrayList
argument_list|<
name|ValueAggregatorDescriptor
argument_list|>
name|aggregatorDescriptorList
init|=
literal|null
decl_stmt|;
DECL|method|setup (Configuration job)
specifier|public
specifier|static
name|void
name|setup
parameter_list|(
name|Configuration
name|job
parameter_list|)
block|{
name|initializeMySpec
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|logSpec
argument_list|()
expr_stmt|;
block|}
DECL|method|getValueAggregatorDescriptor ( String spec, Configuration conf)
specifier|protected
specifier|static
name|ValueAggregatorDescriptor
name|getValueAggregatorDescriptor
parameter_list|(
name|String
name|spec
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|spec
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
index|[]
name|segments
init|=
name|spec
operator|.
name|split
argument_list|(
literal|","
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|segments
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|compareToIgnoreCase
argument_list|(
literal|"UserDefined"
argument_list|)
operator|==
literal|0
condition|)
block|{
name|String
name|className
init|=
name|segments
index|[
literal|1
index|]
decl_stmt|;
return|return
operator|new
name|UserDefinedValueAggregatorDescriptor
argument_list|(
name|className
argument_list|,
name|conf
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getAggregatorDescriptors ( Configuration conf)
specifier|protected
specifier|static
name|ArrayList
argument_list|<
name|ValueAggregatorDescriptor
argument_list|>
name|getAggregatorDescriptors
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|num
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DESCRIPTOR_NUM
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|ValueAggregatorDescriptor
argument_list|>
name|retv
init|=
operator|new
name|ArrayList
argument_list|<
name|ValueAggregatorDescriptor
argument_list|>
argument_list|(
name|num
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|spec
init|=
name|conf
operator|.
name|get
argument_list|(
name|DESCRIPTOR
operator|+
literal|"."
operator|+
name|i
argument_list|)
decl_stmt|;
name|ValueAggregatorDescriptor
name|ad
init|=
name|getValueAggregatorDescriptor
argument_list|(
name|spec
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|ad
operator|!=
literal|null
condition|)
block|{
name|retv
operator|.
name|add
argument_list|(
name|ad
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|retv
return|;
block|}
DECL|method|initializeMySpec (Configuration conf)
specifier|private
specifier|static
name|void
name|initializeMySpec
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|aggregatorDescriptorList
operator|=
name|getAggregatorDescriptors
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|aggregatorDescriptorList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|aggregatorDescriptorList
operator|.
name|add
argument_list|(
operator|new
name|UserDefinedValueAggregatorDescriptor
argument_list|(
name|ValueAggregatorBaseDescriptor
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logSpec ()
specifier|protected
specifier|static
name|void
name|logSpec
parameter_list|()
block|{   }
block|}
end_class

end_unit

