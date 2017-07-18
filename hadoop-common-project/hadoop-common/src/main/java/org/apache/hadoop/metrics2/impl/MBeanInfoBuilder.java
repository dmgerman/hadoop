begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanAttributeInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanInfo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|metrics2
operator|.
name|AbstractMetric
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
name|metrics2
operator|.
name|MetricsInfo
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
name|metrics2
operator|.
name|MetricsTag
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
name|metrics2
operator|.
name|MetricsVisitor
import|;
end_import

begin_comment
comment|/**  * Helper class to build MBeanInfo from metrics records  */
end_comment

begin_class
DECL|class|MBeanInfoBuilder
class|class
name|MBeanInfoBuilder
implements|implements
name|MetricsVisitor
block|{
DECL|field|name
DECL|field|description
specifier|private
specifier|final
name|String
name|name
decl_stmt|,
name|description
decl_stmt|;
DECL|field|attrs
specifier|private
name|List
argument_list|<
name|MBeanAttributeInfo
argument_list|>
name|attrs
decl_stmt|;
DECL|field|recs
specifier|private
name|Iterable
argument_list|<
name|MetricsRecordImpl
argument_list|>
name|recs
decl_stmt|;
DECL|field|curRecNo
specifier|private
name|int
name|curRecNo
decl_stmt|;
DECL|method|MBeanInfoBuilder (String name, String desc)
name|MBeanInfoBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|description
operator|=
name|desc
expr_stmt|;
name|attrs
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
DECL|method|reset (Iterable<MetricsRecordImpl> recs)
name|MBeanInfoBuilder
name|reset
parameter_list|(
name|Iterable
argument_list|<
name|MetricsRecordImpl
argument_list|>
name|recs
parameter_list|)
block|{
name|this
operator|.
name|recs
operator|=
name|recs
expr_stmt|;
name|attrs
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|newAttrInfo (String name, String desc, String type)
name|MBeanAttributeInfo
name|newAttrInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|,
name|String
name|type
parameter_list|)
block|{
return|return
operator|new
name|MBeanAttributeInfo
argument_list|(
name|getAttrName
argument_list|(
name|name
argument_list|)
argument_list|,
name|type
argument_list|,
name|desc
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
comment|// read-only, non-is
block|}
DECL|method|newAttrInfo (MetricsInfo info, String type)
name|MBeanAttributeInfo
name|newAttrInfo
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|String
name|type
parameter_list|)
block|{
return|return
name|newAttrInfo
argument_list|(
name|info
operator|.
name|name
argument_list|()
argument_list|,
name|info
operator|.
name|description
argument_list|()
argument_list|,
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|gauge (MetricsInfo info, int value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|attrs
operator|.
name|add
argument_list|(
name|newAttrInfo
argument_list|(
name|info
argument_list|,
literal|"java.lang.Integer"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|gauge (MetricsInfo info, long value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|attrs
operator|.
name|add
argument_list|(
name|newAttrInfo
argument_list|(
name|info
argument_list|,
literal|"java.lang.Long"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|gauge (MetricsInfo info, float value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|attrs
operator|.
name|add
argument_list|(
name|newAttrInfo
argument_list|(
name|info
argument_list|,
literal|"java.lang.Float"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|gauge (MetricsInfo info, double value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|double
name|value
parameter_list|)
block|{
name|attrs
operator|.
name|add
argument_list|(
name|newAttrInfo
argument_list|(
name|info
argument_list|,
literal|"java.lang.Double"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|counter (MetricsInfo info, int value)
specifier|public
name|void
name|counter
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|attrs
operator|.
name|add
argument_list|(
name|newAttrInfo
argument_list|(
name|info
argument_list|,
literal|"java.lang.Integer"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|counter (MetricsInfo info, long value)
specifier|public
name|void
name|counter
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|attrs
operator|.
name|add
argument_list|(
name|newAttrInfo
argument_list|(
name|info
argument_list|,
literal|"java.lang.Long"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getAttrName (String name)
name|String
name|getAttrName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|curRecNo
operator|>
literal|0
condition|?
name|name
operator|+
literal|"."
operator|+
name|curRecNo
else|:
name|name
return|;
block|}
DECL|method|get ()
name|MBeanInfo
name|get
parameter_list|()
block|{
name|curRecNo
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|MetricsRecordImpl
name|rec
range|:
name|recs
control|)
block|{
for|for
control|(
name|MetricsTag
name|t
range|:
name|rec
operator|.
name|tags
argument_list|()
control|)
block|{
name|attrs
operator|.
name|add
argument_list|(
name|newAttrInfo
argument_list|(
literal|"tag."
operator|+
name|t
operator|.
name|name
argument_list|()
argument_list|,
name|t
operator|.
name|description
argument_list|()
argument_list|,
literal|"java.lang.String"
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AbstractMetric
name|m
range|:
name|rec
operator|.
name|metrics
argument_list|()
control|)
block|{
name|m
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
operator|++
name|curRecNo
expr_stmt|;
block|}
name|MetricsSystemImpl
operator|.
name|LOG
operator|.
name|debug
argument_list|(
name|attrs
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|MBeanAttributeInfo
index|[]
name|attrsArray
init|=
operator|new
name|MBeanAttributeInfo
index|[
name|attrs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
operator|new
name|MBeanInfo
argument_list|(
name|name
argument_list|,
name|description
argument_list|,
name|attrs
operator|.
name|toArray
argument_list|(
name|attrsArray
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
comment|// no ops/ctors/notifications
block|}
block|}
end_class

end_unit

