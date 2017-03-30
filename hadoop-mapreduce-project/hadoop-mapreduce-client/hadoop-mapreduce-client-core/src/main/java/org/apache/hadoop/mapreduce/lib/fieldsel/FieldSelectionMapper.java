begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.fieldsel
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
name|fieldsel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|List
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|Text
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
name|mapreduce
operator|.
name|Mapper
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
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|TextInputFormat
import|;
end_import

begin_comment
comment|/**  * This class implements a mapper class that can be used to perform  * field selections in a manner similar to unix cut. The input data is treated  * as fields separated by a user specified separator (the default value is  * "\t"). The user can specify a list of fields that form the map output keys,  * and a list of fields that form the map output values. If the inputformat is  * TextInputFormat, the mapper will ignore the key to the map function. and the  * fields are from the value only. Otherwise, the fields are the union of those  * from the key and those from the value.  *   * The field separator is under attribute "mapreduce.fieldsel.data.field.separator"  *   * The map output field list spec is under attribute   * "mapreduce.fieldsel.map.output.key.value.fields.spec".   * The value is expected to be like  * "keyFieldsSpec:valueFieldsSpec" key/valueFieldsSpec are comma (,) separated  * field spec: fieldSpec,fieldSpec,fieldSpec ... Each field spec can be a   * simple number (e.g. 5) specifying a specific field, or a range (like 2-5)  * to specify a range of fields, or an open range (like 3-) specifying all   * the fields starting from field 3. The open range field spec applies value  * fields only. They have no effect on the key fields.  *   * Here is an example: "4,3,0,1:6,5,1-3,7-". It specifies to use fields  * 4,3,0 and 1 for keys, and use fields 6,5,1,2,3,7 and above for values.  */
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
DECL|class|FieldSelectionMapper
specifier|public
class|class
name|FieldSelectionMapper
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Mapper
argument_list|<
name|K
argument_list|,
name|V
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|mapOutputKeyValueSpec
specifier|private
name|String
name|mapOutputKeyValueSpec
decl_stmt|;
DECL|field|ignoreInputKey
specifier|private
name|boolean
name|ignoreInputKey
decl_stmt|;
DECL|field|fieldSeparator
specifier|private
name|String
name|fieldSeparator
init|=
literal|"\t"
decl_stmt|;
DECL|field|mapOutputKeyFieldList
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|mapOutputKeyFieldList
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|mapOutputValueFieldList
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|mapOutputValueFieldList
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allMapValueFieldsFrom
specifier|private
name|int
name|allMapValueFieldsFrom
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"FieldSelectionMapReduce"
argument_list|)
decl_stmt|;
DECL|method|setup (Context context)
specifier|public
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|this
operator|.
name|fieldSeparator
operator|=
name|conf
operator|.
name|get
argument_list|(
name|FieldSelectionHelper
operator|.
name|DATA_FIELD_SEPARATOR
argument_list|,
literal|"\t"
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapOutputKeyValueSpec
operator|=
name|conf
operator|.
name|get
argument_list|(
name|FieldSelectionHelper
operator|.
name|MAP_OUTPUT_KEY_VALUE_SPEC
argument_list|,
literal|"0-:"
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|ignoreInputKey
operator|=
name|TextInputFormat
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
operator|.
name|equals
argument_list|(
name|context
operator|.
name|getInputFormatClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Input format class not found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|allMapValueFieldsFrom
operator|=
name|FieldSelectionHelper
operator|.
name|parseOutputKeyValueSpec
argument_list|(
name|mapOutputKeyValueSpec
argument_list|,
name|mapOutputKeyFieldList
argument_list|,
name|mapOutputValueFieldList
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|FieldSelectionHelper
operator|.
name|specToString
argument_list|(
name|fieldSeparator
argument_list|,
name|mapOutputKeyValueSpec
argument_list|,
name|allMapValueFieldsFrom
argument_list|,
name|mapOutputKeyFieldList
argument_list|,
name|mapOutputValueFieldList
argument_list|)
operator|+
literal|"\nignoreInputKey:"
operator|+
name|ignoreInputKey
argument_list|)
expr_stmt|;
block|}
comment|/**    * The identify function. Input key/value pair is written directly to output.    */
DECL|method|map (K key, V val, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|val
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|FieldSelectionHelper
name|helper
init|=
operator|new
name|FieldSelectionHelper
argument_list|(
name|FieldSelectionHelper
operator|.
name|emptyText
argument_list|,
name|FieldSelectionHelper
operator|.
name|emptyText
argument_list|)
decl_stmt|;
name|helper
operator|.
name|extractOutputKeyValue
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|,
name|fieldSeparator
argument_list|,
name|mapOutputKeyFieldList
argument_list|,
name|mapOutputValueFieldList
argument_list|,
name|allMapValueFieldsFrom
argument_list|,
name|ignoreInputKey
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
name|helper
operator|.
name|getKey
argument_list|()
argument_list|,
name|helper
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

