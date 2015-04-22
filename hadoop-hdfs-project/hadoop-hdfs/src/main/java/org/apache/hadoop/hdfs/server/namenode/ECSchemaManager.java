begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * This manages EC schemas predefined and activated in the system.  * It loads customized schemas and syncs with persisted ones in  * NameNode image.  *  * This class is instantiated by the FSNamesystem.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
DECL|class|ECSchemaManager
specifier|public
specifier|final
class|class
name|ECSchemaManager
block|{
comment|/**    * TODO: HDFS-8095    */
DECL|field|DEFAULT_DATA_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_DATA_BLOCKS
init|=
literal|6
decl_stmt|;
DECL|field|DEFAULT_PARITY_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_PARITY_BLOCKS
init|=
literal|3
decl_stmt|;
DECL|field|DEFAULT_CODEC_NAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CODEC_NAME
init|=
literal|"rs"
decl_stmt|;
DECL|field|DEFAULT_SCHEMA_NAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_SCHEMA_NAME
init|=
literal|"RS-6-3"
decl_stmt|;
DECL|field|SYS_DEFAULT_SCHEMA
specifier|private
specifier|static
specifier|final
name|ECSchema
name|SYS_DEFAULT_SCHEMA
init|=
operator|new
name|ECSchema
argument_list|(
name|DEFAULT_SCHEMA_NAME
argument_list|,
name|DEFAULT_CODEC_NAME
argument_list|,
name|DEFAULT_DATA_BLOCKS
argument_list|,
name|DEFAULT_PARITY_BLOCKS
argument_list|)
decl_stmt|;
comment|//We may add more later.
DECL|field|SYS_SCHEMAS
specifier|private
specifier|static
name|ECSchema
index|[]
name|SYS_SCHEMAS
init|=
operator|new
name|ECSchema
index|[]
block|{
name|SYS_DEFAULT_SCHEMA
block|}
decl_stmt|;
comment|/**    * All active EC activeSchemas maintained in NN memory for fast querying,    * identified and sorted by its name.    */
DECL|field|activeSchemas
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ECSchema
argument_list|>
name|activeSchemas
decl_stmt|;
DECL|method|ECSchemaManager ()
name|ECSchemaManager
parameter_list|()
block|{
name|this
operator|.
name|activeSchemas
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|ECSchema
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ECSchema
name|schema
range|:
name|SYS_SCHEMAS
control|)
block|{
name|activeSchemas
operator|.
name|put
argument_list|(
name|schema
operator|.
name|getSchemaName
argument_list|()
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
comment|/**      * TODO: HDFS-7859 persist into NameNode      * load persistent schemas from image and editlog, which is done only once      * during NameNode startup. This can be done here or in a separate method.      */
block|}
comment|/**    * Get system defined schemas.    * @return system schemas    */
DECL|method|getSystemSchemas ()
specifier|public
specifier|static
name|ECSchema
index|[]
name|getSystemSchemas
parameter_list|()
block|{
return|return
name|SYS_SCHEMAS
return|;
block|}
comment|/**    * Get system-wide default EC schema, which can be used by default when no    * schema is specified for an EC zone.    * @return schema    */
DECL|method|getSystemDefaultSchema ()
specifier|public
specifier|static
name|ECSchema
name|getSystemDefaultSchema
parameter_list|()
block|{
return|return
name|SYS_DEFAULT_SCHEMA
return|;
block|}
comment|/**    * Tell the specified schema is the system default one or not.    * @param schema    * @return true if it's the default false otherwise    */
DECL|method|isSystemDefault (ECSchema schema)
specifier|public
specifier|static
name|boolean
name|isSystemDefault
parameter_list|(
name|ECSchema
name|schema
parameter_list|)
block|{
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid schema parameter"
argument_list|)
throw|;
block|}
comment|// schema name is the identifier.
return|return
name|SYS_DEFAULT_SCHEMA
operator|.
name|getSchemaName
argument_list|()
operator|.
name|equals
argument_list|(
name|schema
operator|.
name|getSchemaName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get all EC schemas that's available to use.    * @return all EC schemas    */
DECL|method|getSchemas ()
specifier|public
name|ECSchema
index|[]
name|getSchemas
parameter_list|()
block|{
name|ECSchema
index|[]
name|results
init|=
operator|new
name|ECSchema
index|[
name|activeSchemas
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|activeSchemas
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
name|results
argument_list|)
return|;
block|}
comment|/**    * Get the EC schema specified by the schema name.    * @param schemaName    * @return EC schema specified by the schema name    */
DECL|method|getSchema (String schemaName)
specifier|public
name|ECSchema
name|getSchema
parameter_list|(
name|String
name|schemaName
parameter_list|)
block|{
return|return
name|activeSchemas
operator|.
name|get
argument_list|(
name|schemaName
argument_list|)
return|;
block|}
comment|/**    * Clear and clean up    */
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|activeSchemas
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

