begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|HistogramData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|HistogramType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|Statistics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|TickerType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|DynamicMBean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InvalidAttributeValueException
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
name|MBeanException
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
name|javax
operator|.
name|management
operator|.
name|ReflectionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashSet
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * Adapter JMX bean to publish all the Rocksdb metrics.  */
end_comment

begin_class
DECL|class|RocksDBStoreMBean
specifier|public
class|class
name|RocksDBStoreMBean
implements|implements
name|DynamicMBean
block|{
DECL|field|statistics
specifier|private
name|Statistics
name|statistics
decl_stmt|;
DECL|field|histogramAttributes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|histogramAttributes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|RocksDBStoreMBean (Statistics statistics)
specifier|public
name|RocksDBStoreMBean
parameter_list|(
name|Statistics
name|statistics
parameter_list|)
block|{
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
name|histogramAttributes
operator|.
name|add
argument_list|(
literal|"Average"
argument_list|)
expr_stmt|;
name|histogramAttributes
operator|.
name|add
argument_list|(
literal|"Median"
argument_list|)
expr_stmt|;
name|histogramAttributes
operator|.
name|add
argument_list|(
literal|"Percentile95"
argument_list|)
expr_stmt|;
name|histogramAttributes
operator|.
name|add
argument_list|(
literal|"Percentile99"
argument_list|)
expr_stmt|;
name|histogramAttributes
operator|.
name|add
argument_list|(
literal|"StandardDeviation"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAttribute (String attribute)
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attribute
parameter_list|)
throws|throws
name|AttributeNotFoundException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{
for|for
control|(
name|String
name|histogramAttribute
range|:
name|histogramAttributes
control|)
block|{
if|if
condition|(
name|attribute
operator|.
name|endsWith
argument_list|(
literal|"_"
operator|+
name|histogramAttribute
operator|.
name|toUpperCase
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|keyName
init|=
name|attribute
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|attribute
operator|.
name|length
argument_list|()
operator|-
name|histogramAttribute
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|HistogramData
name|histogram
init|=
name|statistics
operator|.
name|getHistogramData
argument_list|(
name|HistogramType
operator|.
name|valueOf
argument_list|(
name|keyName
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Method
name|method
init|=
name|HistogramData
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"get"
operator|+
name|histogramAttribute
argument_list|)
decl_stmt|;
return|return
name|method
operator|.
name|invoke
argument_list|(
name|histogram
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ReflectionException
argument_list|(
name|e
argument_list|,
literal|"Can't read attribute "
operator|+
name|attribute
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|exception
parameter_list|)
block|{
throw|throw
operator|new
name|AttributeNotFoundException
argument_list|(
literal|"No such attribute in RocksDB stats: "
operator|+
name|attribute
argument_list|)
throw|;
block|}
block|}
block|}
try|try
block|{
return|return
name|statistics
operator|.
name|getTickerCount
argument_list|(
name|TickerType
operator|.
name|valueOf
argument_list|(
name|attribute
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AttributeNotFoundException
argument_list|(
literal|"No such attribute in RocksDB stats: "
operator|+
name|attribute
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|setAttribute (Attribute attribute)
specifier|public
name|void
name|setAttribute
parameter_list|(
name|Attribute
name|attribute
parameter_list|)
throws|throws
name|AttributeNotFoundException
throws|,
name|InvalidAttributeValueException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{    }
annotation|@
name|Override
DECL|method|getAttributes (String[] attributes)
specifier|public
name|AttributeList
name|getAttributes
parameter_list|(
name|String
index|[]
name|attributes
parameter_list|)
block|{
name|AttributeList
name|result
init|=
operator|new
name|AttributeList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|attributeName
range|:
name|attributes
control|)
block|{
try|try
block|{
name|Object
name|value
init|=
name|getAttribute
argument_list|(
name|attributeName
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//TODO
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|setAttributes (AttributeList attributes)
specifier|public
name|AttributeList
name|setAttributes
parameter_list|(
name|AttributeList
name|attributes
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|invoke (String actionName, Object[] params, String[] signature)
specifier|public
name|Object
name|invoke
parameter_list|(
name|String
name|actionName
parameter_list|,
name|Object
index|[]
name|params
parameter_list|,
name|String
index|[]
name|signature
parameter_list|)
throws|throws
name|MBeanException
throws|,
name|ReflectionException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getMBeanInfo ()
specifier|public
name|MBeanInfo
name|getMBeanInfo
parameter_list|()
block|{
name|List
argument_list|<
name|MBeanAttributeInfo
argument_list|>
name|attributes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TickerType
name|tickerType
range|:
name|TickerType
operator|.
name|values
argument_list|()
control|)
block|{
name|attributes
operator|.
name|add
argument_list|(
operator|new
name|MBeanAttributeInfo
argument_list|(
name|tickerType
operator|.
name|name
argument_list|()
argument_list|,
literal|"long"
argument_list|,
literal|"RocksDBStat: "
operator|+
name|tickerType
operator|.
name|name
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|HistogramType
name|histogramType
range|:
name|HistogramType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|histogramAttribute
range|:
name|histogramAttributes
control|)
block|{
name|attributes
operator|.
name|add
argument_list|(
operator|new
name|MBeanAttributeInfo
argument_list|(
name|histogramType
operator|.
name|name
argument_list|()
operator|+
literal|"_"
operator|+
name|histogramAttribute
operator|.
name|toUpperCase
argument_list|()
argument_list|,
literal|"long"
argument_list|,
literal|"RocksDBStat: "
operator|+
name|histogramType
operator|.
name|name
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MBeanInfo
argument_list|(
literal|""
argument_list|,
literal|"RocksDBStat"
argument_list|,
name|attributes
operator|.
name|toArray
argument_list|(
operator|new
name|MBeanAttributeInfo
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

