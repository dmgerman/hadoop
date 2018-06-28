begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.files
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|files
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
name|List
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|ValidationFailure
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
name|util
operator|.
name|JsonSerialization
import|;
end_import

begin_comment
comment|/**  * Summary data saved into a {@code _SUCCESS} marker file.  *  * This provides an easy way to determine which committer was used  * to commit work.  *<ol>  *<li>File length == 0: classic {@code FileOutputCommitter}.</li>  *<li>Loadable as {@link SuccessData}:  *   A s3guard committer with name in in {@link #committer} field.</li>  *<li>Not loadable? Something else.</li>  *</ol>  *  * This is an unstable structure intended for diagnostics and testing.  * Applications reading this data should use/check the {@link #name} field  * to differentiate from any other JSON-based manifest and to identify  * changes in the output format.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|SuccessData
specifier|public
class|class
name|SuccessData
extends|extends
name|PersistentCommitData
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
name|SuccessData
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Serialization ID: {@value}.    */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|507133045258460084L
decl_stmt|;
comment|/**    * Name to include in persisted data, so as to differentiate from    * any other manifests: {@value}.    */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.hadoop.fs.s3a.commit.files.SuccessData/1"
decl_stmt|;
comment|/**    * Name of file; includes version marker.    */
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|/** Timestamp of creation. */
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
comment|/** Timestamp as date string; no expectation of parseability. */
DECL|field|date
specifier|private
name|String
name|date
decl_stmt|;
comment|/**    * Host which created the file (implicitly: committed the work).    */
DECL|field|hostname
specifier|private
name|String
name|hostname
decl_stmt|;
comment|/**    * Committer name.    */
DECL|field|committer
specifier|private
name|String
name|committer
decl_stmt|;
comment|/**    * Description text.    */
DECL|field|description
specifier|private
name|String
name|description
decl_stmt|;
comment|/**    * Metrics.    */
DECL|field|metrics
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|metrics
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Diagnostics information.    */
DECL|field|diagnostics
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Filenames in the commit.    */
DECL|field|filenames
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|filenames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|ValidationFailure
block|{
name|ValidationFailure
operator|.
name|verify
argument_list|(
name|name
operator|!=
literal|null
argument_list|,
literal|"Incompatible file format: no 'name' field"
argument_list|)
expr_stmt|;
name|ValidationFailure
operator|.
name|verify
argument_list|(
name|NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"Incompatible file format: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toBytes ()
specifier|public
name|byte
index|[]
name|toBytes
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|serializer
argument_list|()
operator|.
name|toBytes
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|save (FileSystem fs, Path path, boolean overwrite)
specifier|public
name|void
name|save
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
block|{
comment|// always set the name field before being saved.
name|name
operator|=
name|NAME
expr_stmt|;
name|serializer
argument_list|()
operator|.
name|save
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|this
argument_list|,
name|overwrite
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"SuccessData{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"committer='"
argument_list|)
operator|.
name|append
argument_list|(
name|committer
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", hostname='"
argument_list|)
operator|.
name|append
argument_list|(
name|hostname
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", description='"
argument_list|)
operator|.
name|append
argument_list|(
name|description
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", date='"
argument_list|)
operator|.
name|append
argument_list|(
name|date
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", filenames=["
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|join
argument_list|(
name|filenames
argument_list|,
literal|", "
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Dump the metrics (if any) to a string.    * The metrics are sorted for ease of viewing.    * @param prefix prefix before every entry    * @param middle string between key and value    * @param suffix suffix to each entry    * @return the dumped string    */
DECL|method|dumpMetrics (String prefix, String middle, String suffix)
specifier|public
name|String
name|dumpMetrics
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|middle
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
return|return
name|joinMap
argument_list|(
name|metrics
argument_list|,
name|prefix
argument_list|,
name|middle
argument_list|,
name|suffix
argument_list|)
return|;
block|}
comment|/**    * Dump the diagnostics (if any) to a string.    * @param prefix prefix before every entry    * @param middle string between key and value    * @param suffix suffix to each entry    * @return the dumped string    */
DECL|method|dumpDiagnostics (String prefix, String middle, String suffix)
specifier|public
name|String
name|dumpDiagnostics
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|middle
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
return|return
name|joinMap
argument_list|(
name|diagnostics
argument_list|,
name|prefix
argument_list|,
name|middle
argument_list|,
name|suffix
argument_list|)
return|;
block|}
comment|/**    * Join any map of string to value into a string, sorting the keys first.    * @param map map to join    * @param prefix prefix before every entry    * @param middle string between key and value    * @param suffix suffix to each entry    * @return a string for reporting.    */
DECL|method|joinMap (Map<String, ?> map, String prefix, String middle, String suffix)
specifier|protected
specifier|static
name|String
name|joinMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|map
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|middle
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|map
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|*
literal|32
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|k
range|:
name|list
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
name|k
argument_list|)
operator|.
name|append
argument_list|(
name|middle
argument_list|)
operator|.
name|append
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Load an instance from a file, then validate it.    * @param fs filesystem    * @param path path    * @return the loaded instance    * @throws IOException IO failure    * @throws ValidationFailure if the data is invalid    */
DECL|method|load (FileSystem fs, Path path)
specifier|public
specifier|static
name|SuccessData
name|load
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reading success data from {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|SuccessData
name|instance
init|=
name|serializer
argument_list|()
operator|.
name|load
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|instance
operator|.
name|validate
argument_list|()
expr_stmt|;
return|return
name|instance
return|;
block|}
comment|/**    * Get a JSON serializer for this class.    * @return a serializer.    */
DECL|method|serializer ()
specifier|private
specifier|static
name|JsonSerialization
argument_list|<
name|SuccessData
argument_list|>
name|serializer
parameter_list|()
block|{
return|return
operator|new
name|JsonSerialization
argument_list|<>
argument_list|(
name|SuccessData
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/** @return timestamp of creation. */
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
DECL|method|setTimestamp (long timestamp)
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
comment|/** @return timestamp as date; no expectation of parseability. */
DECL|method|getDate ()
specifier|public
name|String
name|getDate
parameter_list|()
block|{
return|return
name|date
return|;
block|}
DECL|method|setDate (String date)
specifier|public
name|void
name|setDate
parameter_list|(
name|String
name|date
parameter_list|)
block|{
name|this
operator|.
name|date
operator|=
name|date
expr_stmt|;
block|}
comment|/**    * @return host which created the file (implicitly: committed the work).    */
DECL|method|getHostname ()
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|hostname
return|;
block|}
DECL|method|setHostname (String hostname)
specifier|public
name|void
name|setHostname
parameter_list|(
name|String
name|hostname
parameter_list|)
block|{
name|this
operator|.
name|hostname
operator|=
name|hostname
expr_stmt|;
block|}
comment|/**    * @return committer name.    */
DECL|method|getCommitter ()
specifier|public
name|String
name|getCommitter
parameter_list|()
block|{
return|return
name|committer
return|;
block|}
DECL|method|setCommitter (String committer)
specifier|public
name|void
name|setCommitter
parameter_list|(
name|String
name|committer
parameter_list|)
block|{
name|this
operator|.
name|committer
operator|=
name|committer
expr_stmt|;
block|}
comment|/**    * @return any description text.    */
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|setDescription (String description)
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/**    * @return any metrics.    */
DECL|method|getMetrics ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getMetrics
parameter_list|()
block|{
return|return
name|metrics
return|;
block|}
DECL|method|setMetrics (Map<String, Long> metrics)
specifier|public
name|void
name|setMetrics
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|metrics
parameter_list|)
block|{
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
block|}
comment|/**    * @return a list of filenames in the commit.    */
DECL|method|getFilenames ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFilenames
parameter_list|()
block|{
return|return
name|filenames
return|;
block|}
DECL|method|setFilenames (List<String> filenames)
specifier|public
name|void
name|setFilenames
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|filenames
parameter_list|)
block|{
name|this
operator|.
name|filenames
operator|=
name|filenames
expr_stmt|;
block|}
DECL|method|getDiagnostics ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
return|;
block|}
DECL|method|setDiagnostics (Map<String, String> diagnostics)
specifier|public
name|void
name|setDiagnostics
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
parameter_list|)
block|{
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
expr_stmt|;
block|}
comment|/**    * Add a diagnostics entry.    * @param key name    * @param value value    */
DECL|method|addDiagnostic (String key, String value)
specifier|public
name|void
name|addDiagnostic
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|diagnostics
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

