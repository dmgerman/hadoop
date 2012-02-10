begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
operator|.
name|db
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
name|mapred
operator|.
name|JobConf
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|DBConfiguration
specifier|public
class|class
name|DBConfiguration
extends|extends
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
name|db
operator|.
name|DBConfiguration
block|{
comment|/** The JDBC Driver class name */
DECL|field|DRIVER_CLASS_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|DRIVER_CLASS_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|DRIVER_CLASS_PROPERTY
decl_stmt|;
comment|/** JDBC Database access URL */
DECL|field|URL_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|URL_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|URL_PROPERTY
decl_stmt|;
comment|/** User name to access the database */
DECL|field|USERNAME_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|USERNAME_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|USERNAME_PROPERTY
decl_stmt|;
comment|/** Password to access the database */
DECL|field|PASSWORD_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|PASSWORD_PROPERTY
decl_stmt|;
comment|/** Input table name */
DECL|field|INPUT_TABLE_NAME_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_TABLE_NAME_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|INPUT_TABLE_NAME_PROPERTY
decl_stmt|;
comment|/** Field names in the Input table */
DECL|field|INPUT_FIELD_NAMES_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_FIELD_NAMES_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|INPUT_FIELD_NAMES_PROPERTY
decl_stmt|;
comment|/** WHERE clause in the input SELECT statement */
DECL|field|INPUT_CONDITIONS_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_CONDITIONS_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|INPUT_CONDITIONS_PROPERTY
decl_stmt|;
comment|/** ORDER BY clause in the input SELECT statement */
DECL|field|INPUT_ORDER_BY_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_ORDER_BY_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|INPUT_ORDER_BY_PROPERTY
decl_stmt|;
comment|/** Whole input query, exluding LIMIT...OFFSET */
DECL|field|INPUT_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_QUERY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|INPUT_QUERY
decl_stmt|;
comment|/** Input query to get the count of records */
DECL|field|INPUT_COUNT_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_COUNT_QUERY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|INPUT_COUNT_QUERY
decl_stmt|;
comment|/** Class name implementing DBWritable which will hold input tuples */
DECL|field|INPUT_CLASS_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_CLASS_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|INPUT_CLASS_PROPERTY
decl_stmt|;
comment|/** Output table name */
DECL|field|OUTPUT_TABLE_NAME_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_TABLE_NAME_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|OUTPUT_TABLE_NAME_PROPERTY
decl_stmt|;
comment|/** Field names in the Output table */
DECL|field|OUTPUT_FIELD_NAMES_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_FIELD_NAMES_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|OUTPUT_FIELD_NAMES_PROPERTY
decl_stmt|;
comment|/** Number of fields in the Output table */
DECL|field|OUTPUT_FIELD_COUNT_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_FIELD_COUNT_PROPERTY
init|=
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
name|db
operator|.
name|DBConfiguration
operator|.
name|OUTPUT_FIELD_COUNT_PROPERTY
decl_stmt|;
comment|/**    * Sets the DB access related fields in the JobConf.      * @param job the job    * @param driverClass JDBC Driver class name    * @param dbUrl JDBC DB access URL.     * @param userName DB access username     * @param passwd DB access passwd    */
DECL|method|configureDB (JobConf job, String driverClass, String dbUrl , String userName, String passwd)
specifier|public
specifier|static
name|void
name|configureDB
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|String
name|driverClass
parameter_list|,
name|String
name|dbUrl
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|passwd
parameter_list|)
block|{
name|job
operator|.
name|set
argument_list|(
name|DRIVER_CLASS_PROPERTY
argument_list|,
name|driverClass
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
name|URL_PROPERTY
argument_list|,
name|dbUrl
argument_list|)
expr_stmt|;
if|if
condition|(
name|userName
operator|!=
literal|null
condition|)
name|job
operator|.
name|set
argument_list|(
name|USERNAME_PROPERTY
argument_list|,
name|userName
argument_list|)
expr_stmt|;
if|if
condition|(
name|passwd
operator|!=
literal|null
condition|)
name|job
operator|.
name|set
argument_list|(
name|PASSWORD_PROPERTY
argument_list|,
name|passwd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the DB access related fields in the JobConf.      * @param job the job    * @param driverClass JDBC Driver class name    * @param dbUrl JDBC DB access URL.     */
DECL|method|configureDB (JobConf job, String driverClass, String dbUrl)
specifier|public
specifier|static
name|void
name|configureDB
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|String
name|driverClass
parameter_list|,
name|String
name|dbUrl
parameter_list|)
block|{
name|configureDB
argument_list|(
name|job
argument_list|,
name|driverClass
argument_list|,
name|dbUrl
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|DBConfiguration (JobConf job)
name|DBConfiguration
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|super
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

