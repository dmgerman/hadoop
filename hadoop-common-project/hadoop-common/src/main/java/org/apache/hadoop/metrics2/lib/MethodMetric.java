begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
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
name|Method
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|MetricsException
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
name|MetricsRecordBuilder
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
name|annotation
operator|.
name|Metric
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|util
operator|.
name|Contracts
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Metric generated from a method, mostly used by annotation  */
end_comment

begin_class
DECL|class|MethodMetric
class|class
name|MethodMetric
extends|extends
name|MutableMetric
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
name|MethodMetric
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|obj
specifier|private
specifier|final
name|Object
name|obj
decl_stmt|;
DECL|field|method
specifier|private
specifier|final
name|Method
name|method
decl_stmt|;
DECL|field|info
specifier|private
specifier|final
name|MetricsInfo
name|info
decl_stmt|;
DECL|field|impl
specifier|private
specifier|final
name|MutableMetric
name|impl
decl_stmt|;
DECL|method|MethodMetric (Object obj, Method method, MetricsInfo info, Metric.Type type)
name|MethodMetric
parameter_list|(
name|Object
name|obj
parameter_list|,
name|Method
name|method
parameter_list|,
name|MetricsInfo
name|info
parameter_list|,
name|Metric
operator|.
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|obj
operator|=
name|checkNotNull
argument_list|(
name|obj
argument_list|,
literal|"object"
argument_list|)
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|checkArg
argument_list|(
name|method
argument_list|,
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|,
literal|"Metric method should have no arguments"
argument_list|)
expr_stmt|;
name|this
operator|.
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|checkNotNull
argument_list|(
name|info
argument_list|,
literal|"info"
argument_list|)
expr_stmt|;
name|impl
operator|=
name|newImpl
argument_list|(
name|checkNotNull
argument_list|(
name|type
argument_list|,
literal|"metric type"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|newImpl (Metric.Type metricType)
specifier|private
name|MutableMetric
name|newImpl
parameter_list|(
name|Metric
operator|.
name|Type
name|metricType
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|resType
init|=
name|method
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|metricType
condition|)
block|{
case|case
name|COUNTER
case|:
return|return
name|newCounter
argument_list|(
name|resType
argument_list|)
return|;
case|case
name|GAUGE
case|:
return|return
name|newGauge
argument_list|(
name|resType
argument_list|)
return|;
case|case
name|DEFAULT
case|:
return|return
name|resType
operator|==
name|String
operator|.
name|class
condition|?
name|newTag
argument_list|(
name|resType
argument_list|)
else|:
name|newGauge
argument_list|(
name|resType
argument_list|)
return|;
case|case
name|TAG
case|:
return|return
name|newTag
argument_list|(
name|resType
argument_list|)
return|;
default|default:
name|checkArg
argument_list|(
name|metricType
argument_list|,
literal|false
argument_list|,
literal|"unsupported metric type"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|newCounter (final Class<?> type)
name|MutableMetric
name|newCounter
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
if|if
condition|(
name|isInt
argument_list|(
name|type
argument_list|)
operator|||
name|isLong
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
operator|new
name|MutableMetric
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|rb
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
try|try
block|{
name|Object
name|ret
init|=
name|method
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|isInt
argument_list|(
name|type
argument_list|)
condition|)
name|rb
operator|.
name|addCounter
argument_list|(
name|info
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|ret
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|rb
operator|.
name|addCounter
argument_list|(
name|info
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|ret
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error invoking method "
operator|+
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Unsupported counter type: "
operator|+
name|type
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|isInt (Class<?> type)
specifier|static
name|boolean
name|isInt
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
name|boolean
name|ret
init|=
name|type
operator|==
name|Integer
operator|.
name|TYPE
operator|||
name|type
operator|==
name|Integer
operator|.
name|class
decl_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|isLong (Class<?> type)
specifier|static
name|boolean
name|isLong
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|type
operator|==
name|Long
operator|.
name|TYPE
operator|||
name|type
operator|==
name|Long
operator|.
name|class
return|;
block|}
DECL|method|isFloat (Class<?> type)
specifier|static
name|boolean
name|isFloat
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|type
operator|==
name|Float
operator|.
name|TYPE
operator|||
name|type
operator|==
name|Float
operator|.
name|class
return|;
block|}
DECL|method|isDouble (Class<?> type)
specifier|static
name|boolean
name|isDouble
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|type
operator|==
name|Double
operator|.
name|TYPE
operator|||
name|type
operator|==
name|Double
operator|.
name|class
return|;
block|}
DECL|method|newGauge (final Class<?> t)
name|MutableMetric
name|newGauge
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|t
parameter_list|)
block|{
if|if
condition|(
name|isInt
argument_list|(
name|t
argument_list|)
operator|||
name|isLong
argument_list|(
name|t
argument_list|)
operator|||
name|isFloat
argument_list|(
name|t
argument_list|)
operator|||
name|isDouble
argument_list|(
name|t
argument_list|)
condition|)
block|{
return|return
operator|new
name|MutableMetric
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|rb
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
try|try
block|{
name|Object
name|ret
init|=
name|method
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|isInt
argument_list|(
name|t
argument_list|)
condition|)
name|rb
operator|.
name|addGauge
argument_list|(
name|info
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|ret
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|isLong
argument_list|(
name|t
argument_list|)
condition|)
name|rb
operator|.
name|addGauge
argument_list|(
name|info
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|ret
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|isFloat
argument_list|(
name|t
argument_list|)
condition|)
name|rb
operator|.
name|addGauge
argument_list|(
name|info
argument_list|,
operator|(
operator|(
name|Float
operator|)
name|ret
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|rb
operator|.
name|addGauge
argument_list|(
name|info
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|ret
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error invoking method "
operator|+
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Unsupported gauge type: "
operator|+
name|t
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|newTag (Class<?> resType)
name|MutableMetric
name|newTag
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|resType
parameter_list|)
block|{
if|if
condition|(
name|resType
operator|==
name|String
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|MutableMetric
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|rb
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
try|try
block|{
name|Object
name|ret
init|=
name|method
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|rb
operator|.
name|tag
argument_list|(
name|info
argument_list|,
operator|(
name|String
operator|)
name|ret
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error invoking method "
operator|+
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Unsupported tag type: "
operator|+
name|resType
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|snapshot (MetricsRecordBuilder builder, boolean all)
annotation|@
name|Override
specifier|public
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|builder
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|impl
operator|.
name|snapshot
argument_list|(
name|builder
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
DECL|method|metricInfo (Method method)
specifier|static
name|MetricsInfo
name|metricInfo
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
return|return
name|Interns
operator|.
name|info
argument_list|(
name|nameFrom
argument_list|(
name|method
argument_list|)
argument_list|,
literal|"Metric for "
operator|+
name|method
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nameFrom (Method method)
specifier|static
name|String
name|nameFrom
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
name|String
name|methodName
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
condition|)
block|{
return|return
name|StringUtils
operator|.
name|capitalize
argument_list|(
name|methodName
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
argument_list|)
return|;
block|}
return|return
name|StringUtils
operator|.
name|capitalize
argument_list|(
name|methodName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

