begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.hadoop.ozone.recon.codegen
package|package
name|org
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|codegen
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|schema
operator|.
name|ReconSchemaDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|codegen
operator|.
name|GenerationTool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|meta
operator|.
name|jaxb
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|meta
operator|.
name|jaxb
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|meta
operator|.
name|jaxb
operator|.
name|Generate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|meta
operator|.
name|jaxb
operator|.
name|Generator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|meta
operator|.
name|jaxb
operator|.
name|Jdbc
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|meta
operator|.
name|jaxb
operator|.
name|Strategy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|meta
operator|.
name|jaxb
operator|.
name|Target
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
name|org
operator|.
name|sqlite
operator|.
name|SQLiteDataSource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|AbstractModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Guice
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
import|;
end_import

begin_comment
comment|/**  * Utility class that generates the Dao and Pojos for Recon schema. The  * implementations of {@link ReconSchemaDefinition} are discovered through  * Guice bindings in order to avoid ugly reflection code, and invoked to  * generate the schema over an embedded database. The jooq code generator then  * runs over the embedded database to generate classes for recon.  */
end_comment

begin_class
DECL|class|JooqCodeGenerator
specifier|public
class|class
name|JooqCodeGenerator
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
name|JooqCodeGenerator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SQLITE_DB
specifier|private
specifier|static
specifier|final
name|String
name|SQLITE_DB
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
operator|+
literal|"/recon-generated-schema"
decl_stmt|;
DECL|field|JDBC_URL
specifier|private
specifier|static
specifier|final
name|String
name|JDBC_URL
init|=
literal|"jdbc:sqlite:"
operator|+
name|SQLITE_DB
decl_stmt|;
DECL|field|allDefinitions
specifier|private
specifier|final
name|Set
argument_list|<
name|ReconSchemaDefinition
argument_list|>
name|allDefinitions
decl_stmt|;
annotation|@
name|Inject
DECL|method|JooqCodeGenerator (Set<ReconSchemaDefinition> allDefinitions)
specifier|public
name|JooqCodeGenerator
parameter_list|(
name|Set
argument_list|<
name|ReconSchemaDefinition
argument_list|>
name|allDefinitions
parameter_list|)
block|{
name|this
operator|.
name|allDefinitions
operator|=
name|allDefinitions
expr_stmt|;
block|}
comment|/**    * Create schema.    */
DECL|method|initializeSchema ()
specifier|private
name|void
name|initializeSchema
parameter_list|()
throws|throws
name|SQLException
block|{
for|for
control|(
name|ReconSchemaDefinition
name|definition
range|:
name|allDefinitions
control|)
block|{
name|definition
operator|.
name|initializeSchema
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Generate entity and DAO classes.    */
DECL|method|generateSourceCode (String outputDir)
specifier|private
name|void
name|generateSourceCode
parameter_list|(
name|String
name|outputDir
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
operator|.
name|withJdbc
argument_list|(
operator|new
name|Jdbc
argument_list|()
operator|.
name|withDriver
argument_list|(
literal|"org.sqlite.JDBC"
argument_list|)
operator|.
name|withUrl
argument_list|(
name|JDBC_URL
argument_list|)
operator|.
name|withUser
argument_list|(
literal|"sa"
argument_list|)
operator|.
name|withPassword
argument_list|(
literal|"sa"
argument_list|)
argument_list|)
operator|.
name|withGenerator
argument_list|(
operator|new
name|Generator
argument_list|()
operator|.
name|withDatabase
argument_list|(
operator|new
name|Database
argument_list|()
operator|.
name|withName
argument_list|(
literal|"org.jooq.meta.sqlite.SQLiteDatabase"
argument_list|)
operator|.
name|withOutputSchemaToDefault
argument_list|(
literal|true
argument_list|)
operator|.
name|withIncludeTables
argument_list|(
literal|true
argument_list|)
operator|.
name|withIncludePrimaryKeys
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|withGenerate
argument_list|(
operator|new
name|Generate
argument_list|()
operator|.
name|withDaos
argument_list|(
literal|true
argument_list|)
operator|.
name|withEmptyCatalogs
argument_list|(
literal|true
argument_list|)
operator|.
name|withEmptySchemas
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|withStrategy
argument_list|(
operator|new
name|Strategy
argument_list|()
operator|.
name|withName
argument_list|(
literal|"org.hadoop.ozone.recon.codegen.TableNamingStrategy"
argument_list|)
argument_list|)
operator|.
name|withTarget
argument_list|(
operator|new
name|Target
argument_list|()
operator|.
name|withPackageName
argument_list|(
literal|"org.hadoop.ozone.recon.schema"
argument_list|)
operator|.
name|withClean
argument_list|(
literal|true
argument_list|)
operator|.
name|withDirectory
argument_list|(
name|outputDir
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|GenerationTool
operator|.
name|generate
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
comment|/**    * Provider for embedded datasource.    */
DECL|class|LocalDataSourceProvider
specifier|static
class|class
name|LocalDataSourceProvider
implements|implements
name|Provider
argument_list|<
name|DataSource
argument_list|>
block|{
DECL|field|db
specifier|private
specifier|static
name|SQLiteDataSource
name|db
decl_stmt|;
static|static
block|{
name|db
operator|=
operator|new
name|SQLiteDataSource
argument_list|()
expr_stmt|;
name|db
operator|.
name|setUrl
argument_list|(
name|JDBC_URL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|DataSource
name|get
parameter_list|()
block|{
return|return
name|db
return|;
block|}
DECL|method|cleanup ()
specifier|static
name|void
name|cleanup
parameter_list|()
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
operator|new
name|File
argument_list|(
name|SQLITE_DB
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing required arguments: "
operator|+
literal|"Need a ouput directory for generated code.\nUsage: "
operator|+
literal|"org.apache.hadoop.ozone.recon.persistence.JooqCodeGenerator "
operator|+
literal|"<outputDirectory>."
argument_list|)
throw|;
block|}
name|String
name|outputDir
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|Injector
name|injector
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|ReconSchemaGenerationModule
argument_list|()
argument_list|,
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|DataSource
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
operator|new
name|LocalDataSourceProvider
argument_list|()
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|JooqCodeGenerator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|JooqCodeGenerator
name|codeGenerator
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|JooqCodeGenerator
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Create tables
try|try
block|{
name|codeGenerator
operator|.
name|initializeSchema
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to initialize schema."
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ExceptionInInitializerError
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// Generate Pojos and Daos
try|try
block|{
name|codeGenerator
operator|.
name|generateSourceCode
argument_list|(
name|outputDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Code generation failed. Aborting build."
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ExceptionInInitializerError
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// Cleanup after
name|LocalDataSourceProvider
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

