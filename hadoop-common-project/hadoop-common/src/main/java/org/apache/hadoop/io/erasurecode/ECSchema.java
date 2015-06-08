begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * Erasure coding schema to housekeeper relevant information.  */
end_comment

begin_class
DECL|class|ECSchema
specifier|public
specifier|final
class|class
name|ECSchema
block|{
DECL|field|NUM_DATA_UNITS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NUM_DATA_UNITS_KEY
init|=
literal|"numDataUnits"
decl_stmt|;
DECL|field|NUM_PARITY_UNITS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NUM_PARITY_UNITS_KEY
init|=
literal|"numParityUnits"
decl_stmt|;
DECL|field|CODEC_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CODEC_NAME_KEY
init|=
literal|"codec"
decl_stmt|;
comment|/**    * A friendly and understandable name that can mean what's it, also serves as    * the identifier that distinguish it from other schemas.    */
DECL|field|schemaName
specifier|private
specifier|final
name|String
name|schemaName
decl_stmt|;
comment|/**    * The erasure codec name associated.    */
DECL|field|codecName
specifier|private
specifier|final
name|String
name|codecName
decl_stmt|;
comment|/**    * Number of source data units coded    */
DECL|field|numDataUnits
specifier|private
specifier|final
name|int
name|numDataUnits
decl_stmt|;
comment|/**    * Number of parity units generated in a coding    */
DECL|field|numParityUnits
specifier|private
specifier|final
name|int
name|numParityUnits
decl_stmt|;
comment|/*    * An erasure code can have its own specific advanced parameters, subject to    * itself to interpret these key-value settings.    */
DECL|field|extraOptions
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extraOptions
decl_stmt|;
comment|/**    * Constructor with schema name and provided all options. Note the options may    * contain additional information for the erasure codec to interpret further.    * @param schemaName schema name    * @param allOptions all schema options    */
DECL|method|ECSchema (String schemaName, Map<String, String> allOptions)
specifier|public
name|ECSchema
parameter_list|(
name|String
name|schemaName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allOptions
parameter_list|)
block|{
assert|assert
operator|(
name|schemaName
operator|!=
literal|null
operator|&&
operator|!
name|schemaName
operator|.
name|isEmpty
argument_list|()
operator|)
assert|;
name|this
operator|.
name|schemaName
operator|=
name|schemaName
expr_stmt|;
if|if
condition|(
name|allOptions
operator|==
literal|null
operator|||
name|allOptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No schema options are provided"
argument_list|)
throw|;
block|}
name|this
operator|.
name|codecName
operator|=
name|allOptions
operator|.
name|get
argument_list|(
name|CODEC_NAME_KEY
argument_list|)
expr_stmt|;
if|if
condition|(
name|codecName
operator|==
literal|null
operator|||
name|codecName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No codec option is provided"
argument_list|)
throw|;
block|}
name|int
name|tmpNumDataUnits
init|=
name|extractIntOption
argument_list|(
name|NUM_DATA_UNITS_KEY
argument_list|,
name|allOptions
argument_list|)
decl_stmt|;
name|int
name|tmpNumParityUnits
init|=
name|extractIntOption
argument_list|(
name|NUM_PARITY_UNITS_KEY
argument_list|,
name|allOptions
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpNumDataUnits
operator|<
literal|0
operator|||
name|tmpNumParityUnits
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No good option for numDataUnits or numParityUnits found "
argument_list|)
throw|;
block|}
name|this
operator|.
name|numDataUnits
operator|=
name|tmpNumDataUnits
expr_stmt|;
name|this
operator|.
name|numParityUnits
operator|=
name|tmpNumParityUnits
expr_stmt|;
name|allOptions
operator|.
name|remove
argument_list|(
name|CODEC_NAME_KEY
argument_list|)
expr_stmt|;
name|allOptions
operator|.
name|remove
argument_list|(
name|NUM_DATA_UNITS_KEY
argument_list|)
expr_stmt|;
name|allOptions
operator|.
name|remove
argument_list|(
name|NUM_PARITY_UNITS_KEY
argument_list|)
expr_stmt|;
comment|// After some cleanup
name|this
operator|.
name|extraOptions
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|allOptions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor with key parameters provided.    * @param schemaName    * @param codecName    * @param numDataUnits    * @param numParityUnits    */
DECL|method|ECSchema (String schemaName, String codecName, int numDataUnits, int numParityUnits)
specifier|public
name|ECSchema
parameter_list|(
name|String
name|schemaName
parameter_list|,
name|String
name|codecName
parameter_list|,
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
block|{
name|this
argument_list|(
name|schemaName
argument_list|,
name|codecName
argument_list|,
name|numDataUnits
argument_list|,
name|numParityUnits
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor with key parameters provided. Note the extraOptions may contain    * additional information for the erasure codec to interpret further.    * @param schemaName    * @param codecName    * @param numDataUnits    * @param numParityUnits    * @param extraOptions    */
DECL|method|ECSchema (String schemaName, String codecName, int numDataUnits, int numParityUnits, Map<String, String> extraOptions)
specifier|public
name|ECSchema
parameter_list|(
name|String
name|schemaName
parameter_list|,
name|String
name|codecName
parameter_list|,
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extraOptions
parameter_list|)
block|{
assert|assert
operator|(
name|schemaName
operator|!=
literal|null
operator|&&
operator|!
name|schemaName
operator|.
name|isEmpty
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|codecName
operator|!=
literal|null
operator|&&
operator|!
name|codecName
operator|.
name|isEmpty
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|numDataUnits
operator|>
literal|0
operator|&&
name|numParityUnits
operator|>
literal|0
operator|)
assert|;
name|this
operator|.
name|schemaName
operator|=
name|schemaName
expr_stmt|;
name|this
operator|.
name|codecName
operator|=
name|codecName
expr_stmt|;
name|this
operator|.
name|numDataUnits
operator|=
name|numDataUnits
expr_stmt|;
name|this
operator|.
name|numParityUnits
operator|=
name|numParityUnits
expr_stmt|;
if|if
condition|(
name|extraOptions
operator|==
literal|null
condition|)
block|{
name|extraOptions
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|// After some cleanup
name|this
operator|.
name|extraOptions
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|extraOptions
argument_list|)
expr_stmt|;
block|}
DECL|method|extractIntOption (String optionKey, Map<String, String> options)
specifier|private
name|int
name|extractIntOption
parameter_list|(
name|String
name|optionKey
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
parameter_list|)
block|{
name|int
name|result
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
if|if
condition|(
name|options
operator|.
name|containsKey
argument_list|(
name|optionKey
argument_list|)
condition|)
block|{
name|result
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|options
operator|.
name|get
argument_list|(
name|optionKey
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bad option value "
operator|+
name|result
operator|+
literal|" found for "
operator|+
name|optionKey
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Option value "
operator|+
name|options
operator|.
name|get
argument_list|(
name|optionKey
argument_list|)
operator|+
literal|" for "
operator|+
name|optionKey
operator|+
literal|" is found. It should be an integer"
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Get the schema name    * @return schema name    */
DECL|method|getSchemaName ()
specifier|public
name|String
name|getSchemaName
parameter_list|()
block|{
return|return
name|schemaName
return|;
block|}
comment|/**    * Get the codec name    * @return codec name    */
DECL|method|getCodecName ()
specifier|public
name|String
name|getCodecName
parameter_list|()
block|{
return|return
name|codecName
return|;
block|}
comment|/**    * Get extra options specific to a erasure code.    * @return extra options    */
DECL|method|getExtraOptions ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getExtraOptions
parameter_list|()
block|{
return|return
name|extraOptions
return|;
block|}
comment|/**    * Get required data units count in a coding group    * @return count of data units    */
DECL|method|getNumDataUnits ()
specifier|public
name|int
name|getNumDataUnits
parameter_list|()
block|{
return|return
name|numDataUnits
return|;
block|}
comment|/**    * Get required parity units count in a coding group    * @return count of parity units    */
DECL|method|getNumParityUnits ()
specifier|public
name|int
name|getNumParityUnits
parameter_list|()
block|{
return|return
name|numParityUnits
return|;
block|}
comment|/**    * Make a meaningful string representation for log output.    * @return string representation    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ECSchema=["
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Name="
operator|+
name|schemaName
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Codec="
operator|+
name|codecName
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NUM_DATA_UNITS_KEY
operator|+
literal|"="
operator|+
name|numDataUnits
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NUM_PARITY_UNITS_KEY
operator|+
literal|"="
operator|+
name|numParityUnits
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|(
name|extraOptions
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
literal|", "
operator|)
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|opt
range|:
name|extraOptions
operator|.
name|keySet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|opt
operator|+
literal|"="
operator|+
name|extraOptions
operator|.
name|get
argument_list|(
name|opt
argument_list|)
operator|+
operator|(
operator|++
name|i
operator|<
name|extraOptions
operator|.
name|size
argument_list|()
condition|?
literal|", "
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ECSchema
name|ecSchema
init|=
operator|(
name|ECSchema
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|numDataUnits
operator|!=
name|ecSchema
operator|.
name|numDataUnits
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|numParityUnits
operator|!=
name|ecSchema
operator|.
name|numParityUnits
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|schemaName
operator|.
name|equals
argument_list|(
name|ecSchema
operator|.
name|schemaName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|codecName
operator|.
name|equals
argument_list|(
name|ecSchema
operator|.
name|codecName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|extraOptions
operator|.
name|equals
argument_list|(
name|ecSchema
operator|.
name|extraOptions
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|schemaName
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|codecName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|extraOptions
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|numDataUnits
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|numParityUnits
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

