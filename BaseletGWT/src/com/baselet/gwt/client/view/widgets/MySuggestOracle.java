package com.baselet.gwt.client.view.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.baselet.gui.AutocompletionText;
import com.google.gwt.user.client.ui.SuggestOracle;

public class MySuggestOracle extends SuggestOracle {

	private static class MySuggestion implements Suggestion {
		
		private AutocompletionText autoCompletionText;
		
		public MySuggestion(AutocompletionText autoCompletionText) {
			this.autoCompletionText = autoCompletionText;
		}

		@Override
		public String getDisplayString() {
			return autoCompletionText.getText() + " <span style='font-style:italic;color:gray'>" + autoCompletionText.getInfo() + "</span>";
		}

		@Override
		public String getReplacementString() {
			return autoCompletionText.getText();
		}

	}

	@Override
	public boolean isDisplayStringHTML() {
		return true;
	}
	
	private List<Suggestion> suggestions = new ArrayList<Suggestion>();

	public void requestSuggestions(final Request request, final Callback callback) {
		String userInput = request.getQuery();
		Collection<Suggestion> result = new LinkedList<Suggestion>();
		for (Suggestion suggestion : suggestions) {
			if (suggestion.getReplacementString().startsWith(userInput)) {
				result.add(highlightUserInput(suggestion, userInput));
			}
		}
		Response response = new Response(result);
		callback.onSuggestionsReady(request, response);
	}

	private Suggestion highlightUserInput(final Suggestion suggestion, final String userInput) {
		return new Suggestion() {
			@Override
			public String getReplacementString() {
				return suggestion.getReplacementString();
			}
			@Override
			public String getDisplayString() {
				return "<strong>" + userInput + "</strong>" + suggestion.getDisplayString().substring(userInput.length());
			}
		};
	}

	public void setAutocompletionList(List<AutocompletionText> autocompletionList) {
		suggestions.clear();
		for (AutocompletionText text : autocompletionList) {
			suggestions.add(new MySuggestion(text));
		}
	}

	private boolean showAllAsDefault = false;
	
	public void setShowAllAsDefault(boolean showAllAsDefault) {
		this.showAllAsDefault = showAllAsDefault;
	}
	
	@Override
	public void requestDefaultSuggestions(final Request request, final Callback callback) {
		if (showAllAsDefault) {
			callback.onSuggestionsReady(request, new Response(suggestions));
		} else {
			super.requestDefaultSuggestions(request, callback);
		}
	}
}