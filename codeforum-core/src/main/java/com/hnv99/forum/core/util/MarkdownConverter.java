package com.hnv99.forum.core.util;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.Arrays;

/**
 * Converts Markdown text to HTML.
 * Usage: Call the static method markdownToHtml with the Markdown text as input.
 */
public class MarkdownConverter {
    /**
     * Converts Markdown text to HTML.
     *
     * @param markdown the Markdown text to be converted
     * @return the HTML representation of the Markdown text
     */
    public static String markdownToHtml(String markdown) {
        // Create a MutableDataSet object to configure options for the Markdown parser
        MutableDataSet options = new MutableDataSet();

        // Add extensions for various Markdown parsers
        options.set(Parser.EXTENSIONS, Arrays.asList(
                AutolinkExtension.create(),     // Autolink extension for converting URLs to links
                EmojiExtension.create(),        // Emoji extension for parsing emoji symbols
                GitLabExtension.create(),       // GitLab-specific Markdown extension
                FootnoteExtension.create(),     // Footnote extension for adding and parsing footnotes
                TaskListExtension.create(),     // Task list extension for creating task lists
                TablesExtension.create()));     // Table extension for parsing and rendering tables

        // Build a Markdown parser with the configured options
        Parser parser = Parser.builder(options).build();
        // Build an HTML renderer with the same options
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // Parse the input Markdown text and render it as HTML
        return renderer.render(parser.parse(markdown));
    }
}
